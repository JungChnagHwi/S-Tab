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

const spaceRoom = {};
const noteRoom = {};
const displayRoom = {};

// 소켓 connection
io.on("connection", (socket) => {
  console.log("User connected:", socket.id);

  // 화면 따라가기
  let displayId = "display-" + socket.id;

  socket.emit("connectionSuccess", {
    socketId: socket.id,
  });

  // 소켓 disconnect
  socket.on("disconnect", () => {
    console.log("User disconnected:", socket.id);

    // space room에서 유저 삭제
    if (socket.spaceId) {
      leaveRoom(spaceRoom, socket.spaceId, "space");
    }

    // note room에서 유저 삭제
    if (socket.noteId) {
      leaveRoom(noteRoom, socket.noteId, "note");
      deleteDisplayRoom(displayId);
    }

    // 화면 따라가기 종료
    if (socket.followId) {
      leaveDisplayRoom(socket.followId);
    }
  });

  // 스페이스 접속
  socket.on("joinSpace", (spaceId, nickname) => {
    // 유저 입장
    spaceRoom[spaceId] = {
      ...spaceRoom[spaceId],
      [socket.id]: {
        nickname,
        color: randomRGB(),
      },
    };

    socket.join(spaceId);
    io.to(spaceId).emit("spaceConnectUser", spaceRoom[spaceId]);
    socket.broadcast.to(spaceId).emit("notifySpace", nickname);
    console.log(`${socket.id} just joined the Space Room ${spaceId}`);

    socket.spaceId = spaceId;
  });

  // 스페이스 종료
  socket.on("leaveSpace", (spaceId) => {
    leaveRoom(spaceRoom, spaceId, "space");
    socket.spaceId = null;
  });

  // 노트 접속
  socket.on("joinNote", (noteId, nickname, color) => {
    // 유저 입장
    noteRoom[noteId] = {
      ...noteRoom[noteId],
      [socket.id]: {
        nickname,
        color,
      },
    };

    socket.join(noteId);
    io.to(noteId).emit("noteConnectUser", noteRoom[noteId]);
    socket.broadcast.to(noteId).emit("notifyNote", nickname);
    console.log(`${socket.id} just joined the Note Room ${noteId}`);

    // 내 화면 따라가기 room 생성
    displayRoom[displayId] = {};
    socket.join(displayId);

    socket.noteId = noteId;
  });

  // 노트 종료
  socket.on("leaveNote", (noteId) => {
    leaveRoom(noteRoom, noteId, "note");
    socket.noteId = null;

    // 화면 따라가기 종료
    if (socket.followId) {
      leaveDisplayRoom(socket.followId);
    }

    // 내 화면 따라가기 종료
    deleteDisplayRoom(displayId);
  });

  // 화면 따라가기 시작
  socket.on("displayFollowing", (socketId, nickname, color) => {
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
  socket.on("stopFollowing", (socketId) => {
    leaveDisplayRoom(socketId);
  });

  // 화면 이동
  socket.on("positionMove", (data) => {
    socket.broadcast.to(displayId).emit("position", data);
  });

  // 스페이스 수정 공유
  socket.on("updateSpace", (spaceId, message) => {
    socket.broadcast.to(spaceId).emit("receiveSpace", message);
  });

  // 노트 수정 공유
  socket.on("updateDrawing", (noteId, message) => {
    socket.broadcast.to(noteId).emit("receiveDrawing", message);
  });

  const toCapitalize = (str) => {
    return str.replace(/\b\w/g, (match) => match.toUpperCase());
  };

  // Room 나가기
  const leaveRoom = (room, roomId, type) => {
    if (room[roomId] && room[roomId][socket.id]) {
      let capType = toCapitalize(type);

      console.log(`${socket.id} left the ${capType} Room ${roomId}`);

      // 유저 정보 삭제
      delete room[roomId][socket.id];
      socket.leave(roomId);
      // 해당 스페이스 방이 비어 있는지 확인
      if (Object.keys(room[roomId]).length === 0) {
        console.log(
          `${capType} Room ${roomId} is now empty and will be deleted.`
        );
        delete room[roomId];
      } else {
        // 나간 유저 정보 알리기
        io.to(roomId).emit(`${type}ConnectUser`, room[roomId]);
      }
    }
  };

  // Display Room 제거
  const deleteDisplayRoom = (displayId) => {
    const sockets = io.sockets.adapter.rooms.get(displayId);

    if (sockets) {
      sockets.forEach((socketId) => {
        const sock = io.sockets.sockets.get(socketId);
        if (sock) {
          console.log(`${socketId} stop following ${socket.id}`);
          sock.leave(displayId);
          sock.emit("exitFollow");
        }
      });
    }

    console.log(`Display Room ${displayId} is now empty and will be deleted.`);
    delete displayRoom[displayId];
  };

  // Display Follow 종료
  const leaveDisplayRoom = (socketId) => {
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
});
