// https://stay-present.tistory.com/107

import { Server } from "socket.io";
import mediasoup from "mediasoup";
import express from "express";
import dotenv from "dotenv";
dotenv.config()
import http from "http";

const app = express(); 
const httpServer = http.createServer(app); 
const PORT = 4000;

const io = new Server(httpServer, {
    cors: {
      origin: "*",
      methods: ["GET", "POST"],
      allowedHeaders: ["my-custom-header"],
      credentials: true
    },
});

const connections = io.of('/sock')

httpServer.listen(PORT, () => {
    console.log(`listening on port: ${PORT}`)
})

let worker
let rooms = {}          // { roomName1: { Router, rooms: [ sicketId1, ... ] }, ...}
let peers = {}          // { socketId1: { roomName1, socket, transports = [id1, id2,] }, producers = [id1, id2,] }, consumers = [id1, id2,], peerDetails }, ...}
let transports = []     // [ { socketId1, roomName1, transport, consumer }, ... ]
let producers = []      // [ { socketId1, roomName1, producer, }, ... ]
let consumers = []      // [ { socketId1, roomName1, consumer, }, ... ]

const createWorker = async () => {
    worker = await mediasoup.createWorker({
      rtcMinPort: 2000,
      rtcMaxPort: 2100,
    })
    console.log(`worker pid ${worker.pid}`)
  
    // mediasoup 내장 함수. worker process 가 예상치 않게 끊겼을 때 'died' 이벤트가 emit된다
    worker.on('died', error => {
      // This implies something serious happened, so kill the application
      console.error('mediasoup worker has died')
      setTimeout(() => process.exit(1), 2000) // exit in 2 seconds
    })
  
    return worker
  }

  //! 가장 먼저해야 하는 작업 : worker 생성 :-) worker가 있어야 router도 transport도 생성할 수 있다.  
worker = createWorker()

const mediaCodecs = [
  {
    kind: 'audio',
    mimeType: 'audio/opus',
    clockRate: 48000,
    channels: 2,
  },
]

// socket 연결
connections.on('connection', async socket => {
    socket.emit('connection-success', {
      socketId: socket.id,
    });

    // 음성 채팅 방 종료
    socket.on('disconnect', () => {
        // 연결이 끊긴 socket 정리
        console.log('peer disconnected')
        consumers = removeItems(consumers, socket.id, 'consumer')
        producers = removeItems(producers, socket.id, 'producer')
        transports = removeItems(transports, socket.id, 'transport')
    
        try{
          const { roomName } = peers[socket.id]
          delete peers[socket.id]
    
          //rooms에서 해당 소켓 정보 삭제
          rooms[roomName] = {
            router: rooms[roomName].router,
            peers: rooms[roomName].peers.filter(socketId => socketId !== socket.id)
          }
        } catch(e) {}
      })

      // 음성 채팅 방 접속
      socket.on('joinRoom', async (roomName, userName, isHost, callback) => {
        socket.join(roomName);
        const router1 = await createRoom(roomName, socket.id)
        peers[socket.id] = {
          socket,
          roomName,           // Name for the Router this Peer joined
          transports: [],
          producers: [],
          consumers: [],
          peerDetails: {
            name: userName,
            isAdmin: isHost, 
          }
        }
        console.log(`${userName} just joined the Room `)
      
        // Router RTP Capabilities
        const rtpCapabilities = router1.rtpCapabilities
    
        // call callback from the client and send back the rtpCapabilities
        callback({ rtpCapabilities })
      })

      // 음성 채팅 방 생성
      const createRoom = async (roomName, socketId) => {
        let router1
        let peers = []
        if (rooms[roomName]) {
          router1 = rooms[roomName].router
          peers = rooms[roomName].peers || []
        } else {
          router1 = await worker.createRouter({ mediaCodecs, })
        }
        
        // console.log(`Router ID: ${router1.id}`, peers.length)
    
        rooms[roomName] = {
          router: router1,
          peers: [...peers, socketId],
        }
    
        return router1
      }

    // socket connection 추가
})

let listenip ;
let announceip ;
if (process.platform === "linux" ) {
   listenip = '0.0.0.0'
   announceip ='0.0.0.0'
}
else {
   listenip = "127.0.0.1"
   announceip = null 
}

const createWebRtcTransport = async (router) => {
    return new Promise(async (resolve, reject) => {
      try {
        const webRtcTransport_options = {
          listenIps: [
            {
              ip: listenip, //!!!! replace with relevant IP address
              announcedIp: announceip
            }
          ],
          enableUdp: true,
          enableTcp: true,
          preferUdp: true,
        }
  
        let transport = await router.createWebRtcTransport(webRtcTransport_options)
        console.log(`transport id: ${transport.id}`)
  
        transport.on('dtlsstatechange', dtlsState => {
          if (dtlsState === 'closed') {
            transport.close()
          }
        })
  
        transport.on('close', () => {
          console.log('transport closed')
        })
  
        resolve(transport)
  
      } catch (error) {
        reject(error)
      }
    })
  }