import { Server } from "socket.io";
import mediasoup from "mediasoup";
import express from "express";
import dotenv from "dotenv";
dotenv.config();
import http from "http";

const app = express();
const httpServer = http.createServer(app);
const PORT = 4000;

const io = new Server(httpServer, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
    allowedHeaders: ["my-custom-header"],
    credentials: true,
  },
});

httpServer.listen(PORT, () => {
  console.log(`listening on port: ${PORT}`);
});

const dataConnections = io.of("/data");
const audioConnections = io.of("/audio");

let dataWorker;
let dataRooms = {};
let dataPeers = {};
let dataTransports = [];
let dataProducers = [];
let dataConsumers = [];

let audioWorker;
let audioRooms = {}; // { roomName1: { Router, rooms: [ sicketId1, ... ] }, ...}
let audioPeers = {}; // { socketId1: { roomName1, socket, transports = [id1, id2,] }, producers = [id1, id2,] }, consumers = [id1, id2,] }, ...}
let audioTransports = []; // [ { socketId1, roomName1, transport, consumer }, ... ]
let audioProducers = []; // [ { socketId1, roomName1, producer, }, ... ]
let audioConsumers = []; // [ { socketId1, roomName1, consumer, }, ... ]

const createDataWorker = async () => {
  dataWorker = await mediasoup.createWorker({
    rtcMinPort: 10001,
    rtcMaxPort: 20000,
  });
  console.log(`data worker pid ${dataWorker.pid}`);

  dataWorker.on("died", (error) => {
    console.error("mediasoup data worker has died");
    setTimeout(() => process.exit(1), 2000);
  });

  return dataWorker;
};

dataWorker = createDataWorker();

const createAudioWorker = async () => {
  audioWorker = await mediasoup.createWorker({
    rtcMinPort: 20001,
    rtcMaxPort: 30000,
  });
  console.log(`audio worker pid ${audioWorker.pid}`);

  // mediasoup 내장 함수. worker process 가 예상치 않게 끊겼을 때 'died' 이벤트가 emit된다
  audioWorker.on("died", (error) => {
    // This implies something serious happened, so kill the application
    console.error("mediasoup audio worker has died");
    setTimeout(() => process.exit(1), 2000); // exit in 2 seconds
  });

  return audioWorker;
};

//! 가장 먼저해야 하는 작업 : worker 생성 :-) worker가 있어야 router도 transport도 생성할 수 있다.
audioWorker = createAudioWorker();

dataConnections.on("connection", async (socket) => {
  socket.emit("connection-success", {
    socketId: socket.id,
  });

  // 데이터 통신 방 종료
  socket.on("disconnect", () => {
    // 연결이 끊긴 socket 정리
    console.log("peer disconnected");
    dataConsumers = removeItems(dataConsumers, socket.id, "consumer");
    dataProducers = removeItems(dataProducers, socket.id, "producer");
    dataTransports = removeItems(dataTransports, socket.id, "transport");

    try {
      const { roomName } = dataPeers[socket.id];
      delete dataPeers[socket.id];

      //rooms에서 해당 소켓 정보 삭제
      dataRooms[roomName] = {
        router: dataRooms[roomName].router,
        peers: dataRooms[roomName].peers.filter(
          (socketId) => socketId !== socket.id
        ),
      };
    } catch (e) {}
  });

  const removeItems = (items, socketId, type) => {
    items.forEach((item) => {
      if (item.socketId === socket.id) {
        item[type].close();
      }
    });
    items = items.filter((item) => item.socketId !== socket.id);

    return items;
  };

  // 데이터 통신 방 접속
  socket.on("joinRoom", async (roomName, userName, callback) => {
    socket.join(roomName);
    const router1 = await createRoom(roomName, socket.id);
    dataPeers[socket.id] = {
      socket,
      roomName, // Name for the Router this Peer joined
      userName,
      transports: [],
      producers: [],
      consumers: [],
    };
    console.log(`${userName} just joined the Room `);

    // Router RTP Capabilities
    const rtpCapabilities = router1.rtpCapabilities;

    // call callback from the client and send back the rtpCapabilities
    callback({ rtpCapabilities });
  });

  // 데이터 통신 방 생성
  const createRoom = async (roomName, socketId) => {
    let router1;
    let peers = [];
    if (dataRooms[roomName]) {
      router1 = dataRooms[roomName].router;
      peers = dataRooms[roomName].peers || [];
    } else {
      router1 = await dataWorker.createRouter({});
    }

    // console.log(`Router ID: ${router1.id}`, peers.length)

    dataRooms[roomName] = {
      router: router1,
      peers: [...peers, socketId],
    };

    return router1;
  };

  // 클라이언트에서 서버측 transport를 생성하기 위해 요청할 때 emit
  socket.on("createWebRtcTransport", async ({ consumer }, callback) => {
    if (!consumer) {
      console.log(
        dataPeers[socket.id].userName,
        " producer로서 createWebRtcTransport 호출"
      );
    } else {
      console.log(
        dataPeers[socket.id].userName,
        " consumer로서 createWebRtcTransport 호출"
      );
    }

    const roomName = dataPeers[socket.id].roomName;
    const router = dataRooms[roomName].router;

    // [체크]
    const [verify] = dataTransports.filter(
      (transport) => transport.socketId === socket.id && !transport.consumer
    );
    // console.log("🔥", verify)

    createWebRtcTransport(router).then(
      (transport) => {
        callback({
          params: {
            id: transport.id,
            iceParameters: transport.iceParameters,
            iceCandidates: transport.iceCandidates,
            dtlsParameters: transport.dtlsParameters,
          },
        });

        // add transport to Peer's properties
        addTransport(transport, roomName, consumer);
      },
      (error) => {
        console.log(error);
      }
    );
  });

  const addTransport = async (transport, roomName, consumer) => {
    dataTransports = [
      ...dataTransports,
      { socketId: socket.id, transport, roomName, consumer },
    ];

    dataPeers[socket.id] = {
      ...dataPeers[socket.id],
      transports: [...dataPeers[socket.id].transports, transport.id],
    };
  };

  const addProducer = (producer, roomName) => {
    dataProducers = [
      ...dataProducers,
      // { socketId: socket.id, producer, roomName, name: peers[socket.id].userName}
      {
        socketId: socket.id,
        producer,
        roomName,
        name: dataPeers[socket.id].userName,
        kind: producer.kind,
      },
    ];
    dataPeers[socket.id] = {
      ...dataPeers[socket.id],
      producers: [...dataPeers[socket.id].producers, producer.id],
    };
  };

  const addConsumer = (consumer, roomName) => {
    dataConsumers = [
      ...dataConsumers,
      { socketId: socket.id, consumer, roomName },
    ];

    dataPeers[socket.id] = {
      ...dataPeers[socket.id],
      consumers: [...dataPeers[socket.id].consumers, consumer.id],
    };
  };

  socket.on("getProducers", (callback) => {
    const { roomName } = dataPeers[socket.id];
    const socketName = dataPeers[socket.id].userName;
    let producerList = [];

    dataProducers.forEach((producerData) => {
      if (
        producerData.socketId !== socket.id &&
        producerData.roomName === roomName
      ) {
        // console.log(`저는 ${dataPeers[socket.id].userName}이고 producerName은 ${dataPeers[producerData.socketId].userName} 이에요! `)
        producerList = [
          ...producerList,
          [
            producerData.producer.id,
            dataPeers[producerData.socketId].userName,
            producerData.socketId,
          ],
        ];
      }
    });
    callback(producerList); // producerList를 담아서 클라이언트측 콜백함수 실행
  });

  // 새로운 producer가 생긴 경우 new-producer 를 emit 해서 consume 할 수 있게 알려줌
  const informConsumers = (roomName, socketId, id) => {
    dataProducers.forEach((producerData) => {
      if (
        producerData.socketId !== socketId &&
        producerData.roomName === roomName
      ) {
        const producerSocket = dataPeers[producerData.socketId].socket;
        // use socket to send producer id to producer
        const socketName = dataPeers[socketId].userName;

        console.log(
          `new-producer emit! socketName: ${socketName}, producerId: ${id}, kind : ${producerData.kind}`
        );
        producerSocket.emit("new-producer", {
          producerId: id,
          socketName: socketName,
          socketId: socketId,
        });
      }
    });
  };
  const getTransport = (socketId) => {
    console.log(
      "getTransport 에서 확인해보는 socketId. 이게 transports 상의 socketId와 같아야해",
      socketId
    );
    const [producerTransport] = dataTransports.filter(
      (transport) => transport.socketId === socketId && !transport.consumer
    );
    try {
      return producerTransport.transport;
    } catch (e) {
      console.log(`getTransport 도중 에러 발생. details : ${e}`);
    }
  };
  let socketConnect = {}; //socket 아이디가 key, value는 Bool
  let socketdataProduce = {}; // socket 아이디가 key, value는 Bool

  socket.on("transport-connect", async ({ dtlsParameters }) => {
    console.log(socket.id, "가 emit('transport-connect', ...) 🔥");
    if (
      getTransport(socket.id).dtlsState !== "connected" ||
      getTransport(socket.id).dtlsState !== "connecting"
    ) {
      try {
        // console.log("찍어나보자..", getTransport(socket.id).dtlsState)
        const tempTransport = getTransport(socket.id);
        if (tempTransport) {
          if (!socketConnect.socketId)
            tempTransport.connect({ dtlsParameters });
          socketConnect.socketId = true; //!임시
          console.log(tempTransport.dtlsState);
        }
      } catch (e) {
        console.log(`transport-connect 도중 에러 발생. details : ${e}`);
      }
    }
  });

  socket.on(
    "transport-produce",
    async ({ kind, rtpParameters, appData, mysocket }, callback) => {
      if (kind == "data" && !socketdataProduce.id) {
        const producer = await getTransport(socket.id).produce({
          kind,
          rtpParameters,
        });
        const id = socket.id;
        if (kind == "data") {
          socketdataProduce.id = true;
        }

        console.log("Producer ID: ", producer.id, producer.kind);

        //todo: 아래 부분 callback 아래쪽으로 옮기고 테스트
        const { roomName } = dataPeers[socket.id];
        addProducer(producer, roomName);
        informConsumers(roomName, socket.id, producer.id);
        producer.on("transportclose", () => {
          console.log("transport for this producer closed ");
          producer.close();
        });
        callback({
          id: producer.id,
          producersExist: dataProducers.length > 1 ? true : false,
        });
      }
    }
  );

  socket.on(
    "transport-recv-connect",
    async ({ dtlsParameters, serverConsumerTransportId }) => {
      const consumerTransport = dataTransports.find(
        (transportData) =>
          transportData.consumer &&
          transportData.transport.id == serverConsumerTransportId
      ).transport;
      console.log(
        "consumerTransport의 dtlsState 확인 🌼🌼🌼",
        consumerTransport.dtlsState
      );
      try {
        await consumerTransport.connect({ dtlsParameters });
      } catch (e) {
        console.log("transport-recv-connect", e);
      }
    }
  );

  socket.on(
    "consume",
    async (
      { rtpCapabilities, remoteProducerId, serverConsumerTransportId },
      callback
    ) => {
      try {
        const { roomName } = dataPeers[socket.id];
        const userName = dataPeers[socket.id].userName;
        const router = dataRooms[roomName].router;

        let consumerTransport = dataTransports.find(
          (transportData) =>
            transportData.consumer &&
            transportData.transport.id == serverConsumerTransportId
        ).transport;

        if (
          router.canConsume({
            producerId: remoteProducerId,
            rtpCapabilities,
          })
        ) {
          // transport can now consume and return a consumer
          const consumer = await consumerTransport.consume({
            producerId: remoteProducerId,
            rtpCapabilities,
            paused: true, //공식문서에서 권고하는 방식. 클라이언트에서 consumer-resume emit 할 때 resume
          });

          consumer.on("transportclose", () => {
            console.log("transport close from consumer");
          });

          consumer.on("producerclose", () => {
            console.log("producer of consumer closed");
            socket.emit("producer-closed", { remoteProducerId });

            consumerTransport.close([]);
            dataTransports = dataTransports.filter(
              (transportData) =>
                transportData.transport.id !== consumerTransport.id
            );
            consumer.close();
            dataConsumers = dataConsumers.filter(
              (consumerData) => consumerData.consumer.id !== consumer.id
            );
          });

          addConsumer(consumer, roomName);

          // from the consumer extract the following params
          // to send back to the Client
          const params = {
            id: consumer.id,
            producerId: remoteProducerId,
            kind: consumer.kind,
            rtpParameters: consumer.rtpParameters,
            serverConsumerId: consumer.id,
            userName: userName,
          };

          // send the parameters to the client
          callback({ params });
        }
      } catch (error) {
        console.log(error.message);
        callback({
          params: {
            error: error,
          },
        });
      }
    }
  );

  socket.on("consumer-resume", async ({ serverConsumerId }) => {
    console.log("consumer resume");
    const { consumer } = dataConsumers.find(
      (consumerData) => consumerData.consumer.id === serverConsumerId
    );
    await consumer.resume();
  });
});

const mediaCodecs = [
  {
    kind: "audio",
    mimeType: "audio/opus",
    clockRate: 48000,
    channels: 2,
  },
];

// 음성 채팅 연결
audioConnections.on("connection", async (socket) => {
  socket.emit("connection-success", {
    socketId: socket.id,
  });

  // 음성 채팅 방 종료
  socket.on("disconnect", () => {
    // 연결이 끊긴 socket 정리
    console.log("peer disconnected");
    audioConsumers = removeItems(audioConsumers, socket.id, "consumer");
    audioProducers = removeItems(audioProducers, socket.id, "producer");
    audioTransports = removeItems(audioTransports, socket.id, "transport");

    try {
      const { roomName } = audioPeers[socket.id];
      delete audioPeers[socket.id];

      //rooms에서 해당 소켓 정보 삭제
      audioRooms[roomName] = {
        router: audioRooms[roomName].router,
        peers: audioRooms[roomName].peers.filter(
          (socketId) => socketId !== socket.id
        ),
      };
    } catch (e) {}
  });

  const removeItems = (items, socketId, type) => {
    items.forEach((item) => {
      if (item.socketId === socket.id) {
        item[type].close();
      }
    });
    items = items.filter((item) => item.socketId !== socket.id);

    return items;
  };

  // 음성 채팅 방 접속
  socket.on("joinRoom", async (roomName, userName, callback) => {
    socket.join(roomName);
    const router1 = await createRoom(roomName, socket.id);
    audioPeers[socket.id] = {
      socket,
      roomName, // Name for the Router this Peer joined
      userName,
      transports: [],
      producers: [],
      consumers: [],
    };
    console.log(`${userName} just joined the Room `);

    // Router RTP Capabilities
    const rtpCapabilities = router1.rtpCapabilities;

    // call callback from the client and send back the rtpCapabilities
    callback({ rtpCapabilities });
  });

  // 음성 채팅 방 생성
  const createRoom = async (roomName, socketId) => {
    let router1;
    let peers = [];
    if (audioRooms[roomName]) {
      router1 = audioRooms[roomName].router;
      peers = audioRooms[roomName].peers || [];
    } else {
      router1 = await audioWorker.createRouter({ mediaCodecs });
    }

    // console.log(`Router ID: ${router1.id}`, peers.length)

    audioRooms[roomName] = {
      router: router1,
      peers: [...peers, socketId],
    };

    return router1;
  };

  // 클라이언트에서 서버측 transport를 생성하기 위해 요청할 때 emit
  socket.on("createWebRtcTransport", async ({ consumer }, callback) => {
    if (!consumer) {
      console.log(
        audioPeers[socket.id].userName,
        " producer로서 createWebRtcTransport 호출"
      );
    } else {
      console.log(
        audioPeers[socket.id].userName,
        " consumer로서 createWebRtcTransport 호출"
      );
    }

    const roomName = audioPeers[socket.id].roomName;
    const router = audioRooms[roomName].router;

    // [체크]
    const [verify] = audioTransports.filter(
      (transport) => transport.socketId === socket.id && !transport.consumer
    );
    // console.log("🔥", verify)

    createWebRtcTransport(router).then(
      (transport) => {
        callback({
          params: {
            id: transport.id,
            iceParameters: transport.iceParameters,
            iceCandidates: transport.iceCandidates,
            dtlsParameters: transport.dtlsParameters,
          },
        });

        // add transport to Peer's properties
        addTransport(transport, roomName, consumer);
      },
      (error) => {
        console.log(error);
      }
    );
  });

  const addTransport = async (transport, roomName, consumer) => {
    audioTransports = [
      ...audioTransports,
      { socketId: socket.id, transport, roomName, consumer },
    ];

    audioPeers[socket.id] = {
      ...audioPeers[socket.id],
      transports: [...audioPeers[socket.id].transports, transport.id],
    };
  };

  const addProducer = (producer, roomName) => {
    audioProducers = [
      ...audioProducers,
      // { socketId: socket.id, producer, roomName, name: peers[socket.id].userName }
      {
        socketId: socket.id,
        producer,
        roomName,
        name: audioPeers[socket.id].userName,
        kind: producer.kind,
      },
    ];
    audioPeers[socket.id] = {
      ...audioPeers[socket.id],
      producers: [...audioPeers[socket.id].producers, producer.id],
    };
  };

  const addConsumer = (consumer, roomName) => {
    audioConsumers = [
      ...audioConsumers,
      { socketId: socket.id, consumer, roomName },
    ];

    audioPeers[socket.id] = {
      ...audioPeers[socket.id],
      consumers: [...audioPeers[socket.id].consumers, consumer.id],
    };
  };

  socket.on("getProducers", (callback) => {
    const { roomName } = audioPeers[socket.id];
    const socketName = audioPeers[socket.id].userName;
    let producerList = [];

    audioProducers.forEach((producerData) => {
      if (
        producerData.socketId !== socket.id &&
        producerData.roomName === roomName
      ) {
        // console.log(`저는 ${audioPeers[socket.id].userName}이고 producerName은 ${audioPeers[producerData.socketId].userName} 이에요! `)
        producerList = [
          ...producerList,
          [
            producerData.producer.id,
            audioPeers[producerData.socketId].userName,
            producerData.socketId,
          ],
        ];
      }
    });
    callback(producerList); // producerList를 담아서 클라이언트측 콜백함수 실행
  });

  // 새로운 producer가 생긴 경우 new-producer 를 emit 해서 consume 할 수 있게 알려줌
  const informConsumers = (roomName, socketId, id) => {
    audioProducers.forEach((producerData) => {
      if (
        producerData.socketId !== socketId &&
        producerData.roomName === roomName
      ) {
        const producerSocket = audioPeers[producerData.socketId].socket;
        // use socket to send producer id to producer
        const socketName = audioPeers[socketId].userName;

        console.log(
          `new-producer emit! socketName: ${socketName}, producerId: ${id}, kind : ${producerData.kind}`
        );
        producerSocket.emit("new-producer", {
          producerId: id,
          socketName: socketName,
          socketId: socketId,
        });
      }
    });
  };
  const getTransport = (socketId) => {
    console.log(
      "getTransport 에서 확인해보는 socketId. 이게 transports 상의 socketId와 같아야해",
      socketId
    );
    const [producerTransport] = audioTransports.filter(
      (transport) => transport.socketId === socketId && !transport.consumer
    );
    try {
      return producerTransport.transport;
    } catch (e) {
      console.log(`getTransport 도중 에러 발생. details : ${e}`);
    }
  };
  let socketConnect = {}; //socket 아이디가 key, value는 Bool
  let socketAudioProduce = {}; // socket 아이디가 key, value는 Bool

  socket.on("transport-connect", async ({ dtlsParameters }) => {
    console.log(socket.id, "가 emit('transport-connect', ...) 🔥");
    if (
      getTransport(socket.id).dtlsState !== "connected" ||
      getTransport(socket.id).dtlsState !== "connecting"
    ) {
      try {
        // console.log("찍어나보자..", getTransport(socket.id).dtlsState)
        const tempTransport = getTransport(socket.id);
        if (tempTransport) {
          if (!socketConnect.socketId)
            tempTransport.connect({ dtlsParameters });
          socketConnect.socketId = true; //!임시
          console.log(tempTransport.dtlsState);
        }
      } catch (e) {
        console.log(`transport-connect 도중 에러 발생. details : ${e}`);
      }
    }
  });

  socket.on(
    "transport-produce",
    async ({ kind, rtpParameters, appData, mysocket }, callback) => {
      if (kind == "audio" && !socketAudioProduce.id) {
        const producer = await getTransport(socket.id).produce({
          kind,
          rtpParameters,
        });
        const id = socket.id;
        if (kind == "audio") {
          socketAudioProduce.id = true;
        }

        console.log("Producer ID: ", producer.id, producer.kind);

        //todo: 아래 부분 callback 아래쪽으로 옮기고 테스트
        const { roomName } = audioPeers[socket.id];
        addProducer(producer, roomName);
        informConsumers(roomName, socket.id, producer.id);
        producer.on("transportclose", () => {
          console.log("transport for this producer closed ");
          producer.close();
        });
        callback({
          id: producer.id,
          producersExist: audioProducers.length > 1 ? true : false,
        });
      }
    }
  );

  socket.on(
    "transport-recv-connect",
    async ({ dtlsParameters, serverConsumerTransportId }) => {
      const consumerTransport = audioTransports.find(
        (transportData) =>
          transportData.consumer &&
          transportData.transport.id == serverConsumerTransportId
      ).transport;
      console.log(
        "consumerTransport의 dtlsState 확인 🌼🌼🌼",
        consumerTransport.dtlsState
      );
      try {
        await consumerTransport.connect({ dtlsParameters });
      } catch (e) {
        console.log("transport-recv-connect", e);
      }
    }
  );

  socket.on(
    "consume",
    async (
      { rtpCapabilities, remoteProducerId, serverConsumerTransportId },
      callback
    ) => {
      try {
        const { roomName } = audioPeers[socket.id];
        const userName = audioPeers[socket.id].userName;
        const router = audioRooms[roomName].router;

        let consumerTransport = audioTransports.find(
          (transportData) =>
            transportData.consumer &&
            transportData.transport.id == serverConsumerTransportId
        ).transport;

        if (
          router.canConsume({
            producerId: remoteProducerId,
            rtpCapabilities,
          })
        ) {
          // transport can now consume and return a consumer
          const consumer = await consumerTransport.consume({
            producerId: remoteProducerId,
            rtpCapabilities,
            paused: true, //공식문서에서 권고하는 방식. 클라이언트에서 consumer-resume emit 할 때 resume
          });

          consumer.on("transportclose", () => {
            console.log("transport close from consumer");
          });

          consumer.on("producerclose", () => {
            console.log("producer of consumer closed");
            socket.emit("producer-closed", { remoteProducerId });

            consumerTransport.close([]);
            audioTransports = audioTransports.filter(
              (transportData) =>
                transportData.transport.id !== consumerTransport.id
            );
            consumer.close();
            audioConsumers = audioConsumers.filter(
              (consumerData) => consumerData.consumer.id !== consumer.id
            );
          });

          addConsumer(consumer, roomName);

          // from the consumer extract the following params
          // to send back to the Client
          const params = {
            id: consumer.id,
            producerId: remoteProducerId,
            kind: consumer.kind,
            rtpParameters: consumer.rtpParameters,
            serverConsumerId: consumer.id,
            userName: userName,
          };

          // send the parameters to the client
          callback({ params });
        }
      } catch (error) {
        console.log(error.message);
        callback({
          params: {
            error: error,
          },
        });
      }
    }
  );

  socket.on("consumer-resume", async ({ serverConsumerId }) => {
    console.log("consumer resume");
    const { consumer } = audioConsumers.find(
      (consumerData) => consumerData.consumer.id === serverConsumerId
    );
    await consumer.resume();
  });
});

const listenip = process.env.LISTEN_IP;
const announceip = process.env.ANNOUNCE_IP;
const stunip = process.env.STUN_IP;
const turnip = process.env.TURN_IP;
const username = process.env.TURN_USERNAME;
const credential = process.env.TURN_CREDENTIAL;

const createWebRtcTransport = async (router) => {
  return new Promise(async (resolve, reject) => {
    try {
      const webRtcTransport_options = {
        listenIps: [
          {
            ip: listenip, //!!!! replace with relevant IP address
            announcedIp: announceip,
          },
        ],
        enableUdp: true,
        enableTcp: true,
        preferUdp: true,
        iceServers: [
          { urls: stunip }, // Google의 공용 STUN 서버
          {
            urls: turnip, // TURN 서버 주소
            username: username, // TURN 서버 유저네임
            credential: credential, // TURN 서버 패스워드
          },
        ],
      };

      let transport = await router.createWebRtcTransport(
        webRtcTransport_options
      );
      console.log(`transport id: ${transport.id}`);

      transport.on("dtlsstatechange", (dtlsState) => {
        if (dtlsState === "closed") {
          console.log("DTLS connection closed");
          transport.close();
        }
      });

      transport.on("close", () => {
        console.log("transport closed");
      });

      resolve(transport);
    } catch (error) {
      console.error("Failed to create WebRTC transport", error);
      reject(error);
    }
  });
};
