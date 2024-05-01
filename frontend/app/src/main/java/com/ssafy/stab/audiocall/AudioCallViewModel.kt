package com.ssafy.stab.audiocall

import android.util.Log
import androidx.lifecycle.ViewModel
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class AudioCallViewModel : ViewModel() {
    private var socket: Socket? = null // 클라이언트 소켓 인스턴스 생성

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

                // 서버로부터 객체를 받았다면 방에 접속 성공!
                if (rtpCapabilities != null) {
                    Log.d("AudioCallViewModel", "Successfully joined room: $roomName with RTP Capabilities: $rtpCapabilities")
                } else {
                    Log.e("AudioCallViewModel", "Failed to join room: $roomName. RTP Capabilities missing.")
                }
            } else {
                Log.e("AudioCallViewModel", "Unexpected response format from server")
            }
        })
    }

    // 소켓 서버와의 연결 끊기
    override fun onCleared() {
        super.onCleared()
        socket?.disconnect()
        Log.d("AudioCallViewModel", "Socket disconnected.")
    }
}