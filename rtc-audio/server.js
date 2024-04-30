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

const connections = io.of("/rtc-audio");

httpServer.listen(PORT, () => {
  console.log(`listening on port: ${PORT}`);
});

let worker;
let rooms = {}; // { roomName1: { Router, rooms: [ sicketId1, ... ] }, ...}
let peers = {}; // { socketId1: { roomName1, socket, transports = [id1, id2,] }, producers = [id1, id2,] }, consumers = [id1, id2,], peerDetails }, ...}
let transports = []; // [ { socketId1, roomName1, transport, consumer }, ... ]
let producers = []; // [ { socketId1, roomName1, producer, }, ... ]
let consumers = []; // [ { socketId1, roomName1, consumer, }, ... ]

const createWorker = async () => {
  worker = await mediasoup.createWorker({
    rtcMinPort: 2000,
    rtcMaxPort: 3000,
  });
  console.log(`worker pid ${worker.pid}`);

  // mediasoup 내장 함수. worker process 가 예상치 않게 끊겼을 때 'died' 이벤트가 emit된다
  worker.on("died", (error) => {
    // This implies something serious happened, so kill the application
    console.error("mediasoup worker has died");
    setTimeout(() => process.exit(1), 2000); // exit in 2 seconds
  });

  return worker;
};

//! 가장 먼저해야 하는 작업 : worker 생성 :-) worker가 있어야 router도 transport도 생성할 수 있다.
worker = createWorker();

const mediaCodecs = [
  {
    kind: "audio",
    mimeType: "audio/opus",
    clockRate: 48000,
    channels: 2,
  },
];

// socket 연결
connections.on("connection", async (socket) => {
  socket.emit("connection-success", {
    socketId: socket.id,
  });

  // 음성 채팅 방 종료
  socket.on("disconnect", () => {
    // 연결이 끊긴 socket 정리
    console.log("peer disconnected");
    consumers = removeItems(consumers, socket.id, "consumer");
    producers = removeItems(producers, socket.id, "producer");
    transports = removeItems(transports, socket.id, "transport");

    try {
      const { roomName } = peers[socket.id];
      delete peers[socket.id];

      //rooms에서 해당 소켓 정보 삭제
      rooms[roomName] = {
        router: rooms[roomName].router,
        peers: rooms[roomName].peers.filter(
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
  socket.on("joinRoom", async (roomName, userName, isHost, callback) => {
    socket.join(roomName);
    const router1 = await createRoom(roomName, socket.id);
    peers[socket.id] = {
      socket,
      roomName, // Name for the Router this Peer joined
      transports: [],
      producers: [],
      consumers: [],
      peerDetails: {
        name: userName,
        isAdmin: isHost,
      },
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
    if (rooms[roomName]) {
      router1 = rooms[roomName].router;
      peers = rooms[roomName].peers || [];
    } else {
      router1 = await worker.createRouter({ mediaCodecs });
    }

    // console.log(`Router ID: ${router1.id}`, peers.length)

    rooms[roomName] = {
      router: router1,
      peers: [...peers, socketId],
    };

    return router1;
  };

  // 클라이언트에서 서버측 transport를 생성하기 위해 요청할 때 emit
  socket.on("createWebRtcTransport", async ({ consumer }, callback) => {
    if (!consumer) {
      console.log(socket.name, " producer로서 createWebRtcTransport 호출");
    } else {
      console.log(socket.name, " consumer로서 createWebRtcTransport 호출");
    }

    const roomName = peers[socket.id].roomName;
    const router = rooms[roomName].router;

    // [체크]
    const [verify] = transports.filter(
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
    transports = [
      ...transports,
      { socketId: socket.id, transport, roomName, consumer },
    ];

    peers[socket.id] = {
      ...peers[socket.id],
      transports: [...peers[socket.id].transports, transport.id],
    };
  };

  const addProducer = (producer, roomName) => {
    producers = [
      ...producers,
      // { socketId: socket.id, producer, roomName, name: peers[socket.id].peerDetails.name}
      {
        socketId: socket.id,
        producer,
        roomName,
        name: socket.name,
        kind: producer.kind,
      },
    ];
    peers[socket.id] = {
      ...peers[socket.id],
      producers: [...peers[socket.id].producers, producer.id],
    };
  };

  const addConsumer = (consumer, roomName) => {
    consumers = [...consumers, { socketId: socket.id, consumer, roomName }];

    peers[socket.id] = {
      ...peers[socket.id],
      consumers: [...peers[socket.id].consumers, consumer.id],
    };
  };

  socket.on("getProducers", (callback) => {
    const { roomName } = peers[socket.id];
    const socketName = peers[socket.id].peerDetails.name;
    let producerList = [];

    producers.forEach((producerData) => {
      if (
        producerData.socketId !== socket.id &&
        producerData.roomName === roomName
      ) {
        // console.log(`저는 ${socket.name}이고 producerName은 ${ peers[producerData.socketId].peerDetails.name} 이에요! `)
        producerList = [
          ...producerList,
          [
            producerData.producer.id,
            peers[producerData.socketId].peerDetails.name,
            producerData.socketId,
            peers[producerData.socketId].peerDetails.isAdmin,
          ],
        ];
      }
    });
    callback(producerList); // producerList를 담아서 클라이언트측 콜백함수 실행
  });

  // 새로운 producer가 생긴 경우 new-producer 를 emit 해서 consume 할 수 있게 알려줌
  const informConsumers = (roomName, socketId, id) => {
    producers.forEach((producerData) => {
      if (
        producerData.socketId !== socketId &&
        producerData.roomName === roomName
      ) {
        const producerSocket = peers[producerData.socketId].socket;
        // use socket to send producer id to producer
        const socketName = peers[socketId].peerDetails.name;
        const isNewSocketHost = peers[socketId].peerDetails.isAdmin;

        console.log(
          `new-producer emit! socketName: ${socketName}, producerId: ${id}, kind : ${producerData.kind}`
        );
        producerSocket.emit("new-producer", {
          producerId: id,
          socketName: socketName,
          socketId: socketId,
          isNewSocketHost,
        });
      }
    });
  };
  const getTransport = (socketId) => {
    console.log(
      "getTransport 에서 확인해보는 socketId. 이게 transports 상의 socketId와 같아야해",
      socketId
    );
    const [producerTransport] = transports.filter(
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
        const { roomName } = peers[socket.id];
        addProducer(producer, roomName);
        informConsumers(roomName, socket.id, producer.id);
        producer.on("transportclose", () => {
          console.log("transport for this producer closed ");
          producer.close();
        });
        callback({
          id: producer.id,
          producersExist: producers.length > 1 ? true : false,
        });
      }
    }
  );

  socket.on(
    "transport-recv-connect",
    async ({ dtlsParameters, serverConsumerTransportId }) => {
      const consumerTransport = transports.find(
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
        const { roomName } = peers[socket.id];
        const userName = peers[socket.id].peerDetails.name;
        const router = rooms[roomName].router;

        let consumerTransport = transports.find(
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
            transports = transports.filter(
              (transportData) =>
                transportData.transport.id !== consumerTransport.id
            );
            consumer.close();
            consumers = consumers.filter(
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
    const { consumer } = consumers.find(
      (consumerData) => consumerData.consumer.id === serverConsumerId
    );
    await consumer.resume();
  });

  // socket connection 추가
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
