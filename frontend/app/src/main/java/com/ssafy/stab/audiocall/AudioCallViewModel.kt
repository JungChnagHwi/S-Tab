package com.ssafy.stab.audiocall

import android.util.Log
import androidx.lifecycle.ViewModel
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import org.mediasoup.droid.Device
import java.net.URISyntaxException

// 연결 상태 확인 로그 추후 삭제 요망
class AudioCallViewModel : ViewModel() {
    private var socket: Socket? = null // 클라이언트 소켓 인스턴스 생성 - socket 통신
//    private var device: Device? = null // 클라이언트 단말 기기 미디어 설정을 위한 디바이스 - webRTC 통신

    // 소켓 서버 초기화
    init {
        try {
            // "http://your_server_address:port"를 실제 서버 주소로 변경해야 합니다.
            socket = IO.socket("https://rtc.s-tab.online/audio")
            Log.d("AudioCallViewModel", "Socket initialized.")
        } catch (e: URISyntaxException) {
            e.printStackTrace() // 주소 형식 오류 발생 시 예외 처리
            Log.e("AudioCallViewModel", "URISyntaxException: ${e.message}")
        }
    }

    // 서버에 연결한 뒤, 정해진 방에 join
    fun connectToServerAndJoinRoom(roomName: String, userName: String) {
        // 소켓 서버와 연결
        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("AudioCallViewModel", "Connected to server")
            // 연결 성공 후 바로 방에 참여
            joinRoom(roomName, userName)
        }?.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e("AudioCallViewModel", "Connection error: ${it.contentToString()}")
        }
    }

    private fun joinRoom(roomName: String, userName: String) {
        // 클라이언트가 서버로 'joinRoon'이벤트를 보내고, callback 함수를 통해 args를 받는다.
        socket?.emit("joinRoom", roomName, userName, Ack { args ->
            Log.d("AudioCallViewModel", "Response from server: ${args}")
            // callback 함수로 받은 args가 비어 있지 않을 때
            if (args.isNotEmpty()) {
                Log.d("AudioCallViewModel", "Response from server: ${args[0]}")
                // { rtpCapabilities } 형태로 오므로 JSONObject로 처리한다.
                val rtpCapabilities = args[0] as JSONObject
                // 서버에서 받은 rtpCapabilities를 이용하여 Device 설정
//                initializeDevice(rtpCapabilities)

                Log.d("AudioCallViewModel", "Successfully joined room: $roomName with RTP Capabilities: $rtpCapabilities")

            } else {
                Log.e("AudioCallViewModel", "Unexpected response format from server")
            }
        })
    }

//    private fun initializeDevice(rtpCapabilities: JSONObject) {
//        device = Device()
//        try {
//            // p2p 연결이 아닌 sfu 구조를 사용해 peerConnectionOptions 가 필요 없다, webRTC 기본 설정 사용(==null)
//            device?.load(rtpCapabilities.toString(), null)
////            createSendTransport()
//        } catch (e: Exception) {
//            Log.e("AudioCallViewModel", "Error loading device: ${e.message}")
//        }
//    }

//    private fun createSendTransport() {
//        // transport 생성
//    }

    // 소켓 서버와의 연결 끊기
    override fun onCleared() {
        super.onCleared()
        socket?.disconnect()
        Log.d("AudioCallViewModel", "Socket disconnected.")
    }
}