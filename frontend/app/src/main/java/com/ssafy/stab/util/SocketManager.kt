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
class SocketManager private constructor() {
    private var socket: Socket? = null
    private val gson = Gson()
    private var isConnected = false

    companion object {
        private var instance: SocketManager? = null

        fun getInstance(): SocketManager {
            if (instance == null) {
                instance = SocketManager()
            }
            return instance!!
        }
    }

    fun connectToSocket(serverUrl: String) {
        if (isConnected) {
            Log.d("SocketManager", "Already connected")
            return
        }

        try {
            socket = IO.socket(serverUrl)
            Log.d("SocketManager", "Socket initialized with server URL: $serverUrl")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Log.e("SocketManager", "URISyntaxException: ${e.message}")
            return
        }

        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("SocketConnection", "Connected")
            isConnected = true
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
            Log.e("SocketConnection", "Connection error: ${error[0]}")
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
            Log.d("SocketConnection", "Disconnected")
            isConnected = false
        }
        socket?.on("connectionSuccess") { data ->
            val socketId = data[0]
            Log.d("SocketConnection", "Connection successful with ID: $socketId")
        }

        registerEventHandlers()

        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
        isConnected = false
    }

    fun joinSpace(spaceId: String, nickname: String) {
        if (isConnected) {
            Log.d("SocketManager", "Joining space with ID: $spaceId and nickname: $nickname")
            socket?.emit("joinSpace", spaceId, nickname)
        } else {
            Log.e("SocketManager", "Socket is not connected")
        }
    }

    fun leaveSpace(spaceId: String) {
        if (isConnected) {
            Log.d("SocketManager", "Leaving space with ID: $spaceId")
            socket?.emit("leaveSpace", spaceId)
        } else {
            Log.e("SocketManager", "Socket is not connected")
        }
    }

    private fun registerEventHandlers() {
        socket?.on("spaceConnectUser") { data ->
            Log.d("SpaceConnection", "SpaceRoom[spaceId] : $data")
        }
        socket?.on("notifySpace") { data ->
            val remoteNickname = data[0]
            Log.d("SpaceConnection", "$remoteNickname just joined the Space Room")
        }
        socket?.on("receiveSpace") { message ->
            val data = message[0]
            Log.d("ReceiveSpaceData", "$data")
        }

        socket?.on("noteConnectUser") { data ->
            Log.d("NoteConnection", "$data")
        }
        socket?.on("notifyNote") { data ->
            val remoteNickname = data[0]
            Log.d("SpaceConnection", "$remoteNickname just joined the Note Room")
        }
        socket?.on("receiveDrawing") { message ->
            val data = message[0]
            Log.d("ReceiveNoteData", "$data")
        }

        socket?.on("followUser") { data ->
            Log.d("FollowUser", "$data")
        }

        socket?.on("position") { data ->
            Log.d("Position", "$data")
        }
    }
}