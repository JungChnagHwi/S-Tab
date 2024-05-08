import { Server } from "socket.io";
import express from "express";
import dotenv from "dotenv";
import { Eureka } from "eureka-js-client";
import http from "http";

dotenv.config();

const app = express();
const httpServer = http.createServer(app);
const PORT = 5442;

const eurekaURL = process.env.EUREKA_SERVICE_URL;
const hostName = process.env.HOST_NAME;
const ipAddr = process.env.IP_ADDR;
const vipAddress = process.env.VIP_ADDR;
const port = process.env.PORT;

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
    vipAddress: vipAddress,
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

const io = new Server(httpServer, {
  cors: {
    origin: "*",
    credentials: true,
  },
});

httpServer.listen(PORT, () => {
  console.log(`listening on port: ${PORT}`);

  eurekaClient.start((error) => {
    console.log("Eureka registration complete");

    if (error) {
      console.log("Eureka registration failed: ", error);
    }
  });
});

const socketRoom = {};

// 소켓 connection
io.on("connection", (socket) => {
  console.log("User connected:", socket.id);

  socket.emit("connection-success", {
    socketId: socket.id,
  });

  // 소켓 종료
  socket.on("disconnect", () => {
    console.log("User disconnected:", socket.id);
  });

  // 소켓 접속
  socket.on("joinRoom", (spaceId, nickname) => {
    try {
      socketRoom[spaceId] = {
        ...socketRoom[spaceId],
        nickname,
      };

      // 유저 입장
      socket.join(spaceId);
      io.to(spaceId).emit("connectUser", socketRoom[spaceId]);
      console.log(`${nickname} just joined the Room `);
    } catch (error) {
      console.log(error);
    }

    try {
      socket.broadcast.to(spaceId).emit("notify", nickname);
    } catch (error) {
      console.log(error);
    }
  });

  socket.on("leaveRoom", (spaceId, nickname) => {
    try {
      console.log("socket room leave:", spaceId, nickname);

      // 유저 정보 삭제
      delete socketRoom[spaceId][nickname];

      // 나간 유저 정보 알리기
      io.to(spaceId).emit("connectUser", socketRoom[spaceId]);
      socket.leave(spaceId);
    } catch (error) {
      console.log(error);
    }
  });
});
