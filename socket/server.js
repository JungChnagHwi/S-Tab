import { Server } from "socket.io";
import express from "express";
import dotenv from "dotenv";
import { Eureka } from "eureka-js-client";

dotenv.config();

const app = express();
const PORT = 5442;

const hostName = process.env.HOST_NAME;
const ipAddr = process.env.IP_ADDR;
const port = process.env.PORT;
const vipAddr = process.env.VIP_ADDR;
const eurekaURL = process.env.EUREKA_SERVICE_URL;

const eurekaClient = new Eureka({
  instance: {
    app: "socket",
    instanceId: "socket",
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: port,
      "@enabled": true,
    },
    vipAddress: vipAddr,
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn",
    },
  },
  eureka: {
    host: eurekaURL,
    port: 8761,
    servicePath: "/eureka/apps/",
    fetchRegistry: true,
    registerWithEureka: true,
  },
});

const httpServer = app.listen(PORT, () => {
  console.log(`listening on port: ${PORT}`);

  eurekaClient.start((error) => {
    console.log("Eureka registration complete");

    if (error) {
      console.log("Eureka registration failed: ", error);
    }
  });
});

const io = new Server(httpServer, {
  cors: {
    origin: "*",
    credentials: true,
  },
});

// 랜덤 color 생성
const randomRGB = () => {
  let rgb = "";
  rgb += Math.floor(Math.random() * 256)
    .toString(16)
    .padStart(2, "0");
  rgb += Math.floor(Math.random() * 256)
    .toString(16)
    .padStart(2, "0");
  rgb += Math.floor(Math.random() * 256)
    .toString(16)
    .padStart(2, "0");
  return "#" + rgb;
};

const noteRoom = {};
const displayRoom = {};

// 소켓 connection
io.on("connection", (socket) => {
  console.log("User connected:", socket.id);

  // 화면 따라가기
  let displayId = "display-" + socket.id;
  displayRoom[displayId] = {};
  socket.join(displayId);

  socket.emit("connectionSuccess", {
    socketId: socket.id,
  });

  // 소켓 disconnect
  socket.on("disconnect", () => {
    console.log("User disconnected:", socket.id);

    // note room에서 유저 삭제
    if (socket.noteId) {
      deleteNoteRoom(socket.noteId);
    }

    // 화면 따라가기 종료
    if (socket.followId) {
      deleteDisplayRoom(socket.followId);
    }

    delete displayRoom[displayId];
    io.to(displayId).emit("exitFollow");
  });

  // note room 접속
  socket.on("joinNote", (noteId, nickname) => {
    // 유저 입장
    noteRoom[noteId] = {
      ...noteRoom[noteId],
      [socket.id]: {
        nickname,
        color: randomRGB(),
      },
    };

    socket.join(noteId);
    io.to(noteId).emit("connectUser", noteRoom[noteId]);
    socket.broadcast.to(noteId).emit("notify", nickname);
    console.log(`${socket.id} just joined the Room ${noteId}`);

    socket.noteId = noteId;
  });

  // note room 종료
  socket.on("leaveNote", (noteId) => {
    deleteNoteRoom(noteId);

    // 화면 따라가기 종료
    socket.broadcast.to(displayId).emit("exitFollow");
    socket.noteId = null;
  });

  const deleteNoteRoom = (noteId) => {
    console.log(`${socket.id} leaved the Room ${noteId}`);

    // 유저 정보 삭제
    delete noteRoom[noteId][socket.id];

    // 나간 유저 정보 알리기
    io.to(noteId).emit("connectUser", noteRoom[noteId]);
    socket.leave(noteId);

    // 해당 노트 방이 비어 있는지 확인
    if (Object.keys(noteRoom[noteId]).length === 0) {
      console.log(`Room ${noteId} is now empty and will be deleted.`);
      delete noteRoom[noteId];
    }
  };

  // 화면 따라가기 시작
  socket.on("displayFollow", (socketId, nickname, color) => {
    let followDisplayId = "display-" + socketId;

    displayRoom[followDisplayId] = {
      ...displayRoom[followDisplayId],
      [socket.id]: {
        nickname,
        color,
      },
    };

    socket.join(followDisplayId);
    io.to(socketId).emit("followUser", displayRoom[followDisplayId]);
    console.log(`${socket.id} follows ${socketId}`);

    socket.followId = socketId;
  });

  // 화면 따라가기 종료
  socket.on("stopFollow", (socketId) => {
    deleteDisplayRoom(socketId);
  });

  const deleteDisplayRoom = (socketId) => {
    let followDisplayId = "display-" + socketId;

    if (
      displayRoom[followDisplayId] &&
      displayRoom[followDisplayId][socket.id]
    ) {
      console.log(`${socket.id} stop following ${socketId}`);

      delete displayRoom[followDisplayId][socket.id];

      io.to(socketId).emit("followUser", displayRoom[followDisplayId]);
    }

    socket.leave(followDisplayId);
    socket.followId = null;
  };

  // 화면 이동
  socket.on("positionMove", (data) => {
    socket.broadcast.to(displayId).emit("position", data);
  });
});
