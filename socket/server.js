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

const socketRoom = {};

// 소켓 connection
io.on("connection", (socket) => {
  console.log("User connected:", socket.id);

  socket.emit("connection-success", {
    socketId: socket.id,
  });

  // 소켓 disconnect
  socket.on("disconnect", () => {
    console.log("User disconnected:", socket.id);
  });

  // room 접속
  socket.on("join-room", (roomId, socketId, nickname) => {
    try {
      socketRoom[roomId] = {
        ...socketRoom[roomId],
        [socketId]: {
          nickname,
          color: randomRGB(),
        },
      };

      // 유저 입장
      socket.join(roomId);
      io.to(roomId).emit("connect-user", socketRoom[roomId]);
      socket.broadcast.to(roomId).emit("notify", nickname);
      console.log(`${nickname} just joined the Room ${roomId}`);
    } catch (error) {
      console.log(error);
    }
  });

  // room 종료
  socket.on("leave-room", (roomId, nickname) => {
    try {
      console.log(`${nickname} leaved the Room ${roomId}`);

      // 유저 정보 삭제
      delete socketRoom[roomId][nickname];

      // 나간 유저 정보 알리기
      io.to(roomId).emit("connect-user", socketRoom[roomId]);
      socket.leave(roomId);
    } catch (error) {
      console.log(error);
    }
  });
});
