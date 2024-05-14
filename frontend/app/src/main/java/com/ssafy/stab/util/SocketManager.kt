package com.ssafy.stab.util

import android.util.Log
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

/*
    on: 이벤트 받기, emit: 이벤트 보내기
    이벤트 보내기 : node.js와 데이터 호환성을 위해 json 형태로 데이터를 보냅니다.
    이벤트 받기 : json으로 보냈던 데이터를 받아 본인이 사용할 데이터 형태로 바꿉니다.
*/
class SocketManager {
    private var socket: Socket? = null
    private val gson = Gson()

    // 소켓 연결 설정
    fun connectToSocket(serverUrl: String) {
        try {
            socket = IO.socket(BuildConfig.SOCKET_URL) // 소켓 서버 주소 설정
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        // 소켓 연결
        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("SocketConnection", "Connected")
        }
        // 소켓 연결 끊기
        socket?.on(Socket.EVENT_DISCONNECT) {
            Log.d("SocketConnection", "Disconnected")
        }
        // socketId에 연결됨을 확인
        socket?.on("connectionSuccess") { data ->
            val socketId = data[0]
            Log.d("SocketConnection",  "Connection successful with ID: $socketId")
        }

        registerEventHandlers() // 추가 이벤트 핸들러 등록

        socket?.connect() // 실제 소켓과 연결
    }

    fun disconnect() {
        socket?.disconnect() // 소켓 연결 끊기
    }

    // 스페이스 room 참여/떠나기
    fun joinSpace(spaceId: String, nickname: String) {
        socket?.emit("joinSpace", spaceId, nickname)
    }

    fun leaveSpace(spaceId: String) {
        socket?.emit("leaveSpace", spaceId)
    }

    // 스페이스 이벤트 업데이트 공유
    fun updateSpace(spaceId: String, message: Any) {
        val jsonData = gson.toJson(message)
        socket?.emit("updateSpace", spaceId, jsonData )
    }

    // 노트 room 참여/떠나기
    fun joinNote(noteId: String, nickName: String, color: String) {
        socket?.emit("joinNote", noteId, nickName, color)
    }

    fun leaveNote(noteId: String) {
        socket?.emit("leaveNote", noteId)
    }
    // 노트 이벤트 업데이트 공유
    fun updateDrawing(noteId: String, message: Any) {
        val jsonData = gson.toJson(message)
        socket?.emit("updateDrawing", noteId, jsonData )
    }

    // 화면 따라가기 시작
    fun displayFollowing(socketId: String, nickname: String, color: String) {
        socket?.emit("displayFollowing", socketId, nickname, color)
    }

    // 화면 따라가기 종료
    fun stopFollowing(socketId: String) {
        socket?.emit("stopFollowing", socketId)
    }

    // 화면 이동
    fun positionMove(data: Any) {
        // 데이터를 JSON으로 변환해 전송
        val jsonData = gson.toJson(data)
        socket?.emit("positionMove", jsonData)
    }

    private fun registerEventHandlers() {
        // space 관련 서버 이벤트 수신
        socket?.on("spaceConnectUser") { data ->
            Log.d("SpaceConnection", "SpaceRoom[spaceId] : $data")
        }
        // space room에 입장한 사용자 닉네임 받기
        socket?.on("notifySpace") { data ->
            val remoteNickname = data[0]
            Log.d("SpaceConnection", "$remoteNickname just joined the Space Room")
        }
        // space 이벤트 받기
        socket?.on("receiveSpace") { message ->
            val data = message[0]
            Log.d("ReceiveSpaceData", "$data")
        }

        // note 관련 서버 이벤트 수신
        socket?.on("noteConnectUser") { data ->
            Log.d("NoteConnection", "$data")
        }
        // note room에 입장한 사용자 닉네임 받기
        socket?.on("notifyNote") { data ->
            val remoteNickname = data[0]
            Log.d("SpaceConnection", "$remoteNickname just joined the Note Room")
        }
        // note 이벤트 받기
        socket?.on("receiveDrawing") { message ->
            val data = message[0]
            Log.d("ReceiveNoteData", "$data")
        }

        // followUser 이벤트 핸들러 - 보류
        socket?.on("followUser") {data ->
            Log.d("FollowUser", "$data")
        }

        // 화면 이동 위치 전송 받기 - 보류 (사유: data가 어떻게 오는지, 사용법을 잘 모르겠음)
        socket?.on("position") { data ->
            Log.d("Position", "$data")
        }

    }

    // 화면 따라가는 로직 구현 필요 - 근데 백 서버에서 좌표 계속 받아야 할 것 같은데 구현되어 있는지 모르겠음
    private fun handleFollowUser(data: JSONObject) {
    }
}