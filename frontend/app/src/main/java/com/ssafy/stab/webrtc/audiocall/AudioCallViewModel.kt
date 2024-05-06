package com.ssafy.stab.webrtc.audiocall


import android.content.Context
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.webrtc.openvidu.RemoteParticipant
import com.ssafy.stab.webrtc.openvidu.Session
import com.ssafy.stab.webrtc.utils.CustomHttpClient
import com.ssafy.stab.webrtc.utils.PermissionManager
import com.ssafy.stab.webrtc.websocket.CustomWebSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.webrtc.MediaStream
import java.io.IOException

// session 접속 데이터를 다루는 viewmodel
class AudioCallViewModel : ViewModel(), CustomWebSocket.WebSocketCallback, Session.StreamObserver {
    var sessionId = mutableStateOf("")
    var participantName = mutableStateOf("")
    private val serverUrl = "https://demos.openvidu.io"
    var participants = mutableStateOf(listOf<String>())
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    private var session: Session? = null
    private var httpClient: CustomHttpClient? = null

    // 오디오 권한 체크 후, 세션 id와 서버 url이 유효한지 확인하고 session 입장 요청
    fun buttonPressed(context: Context) {
        if (_isConnected.value) {
            leaveSession()
            return
        }

        if (PermissionManager.arePermissionsGranted(context)) {
            val currentSessionId = sessionId.value
            // 서버 Url을 담은 customhttpclient 생성
            httpClient = CustomHttpClient(serverUrl)
            getToken(currentSessionId)
        }
        else {
            _errorMessage.value = "오디오 권한이 필요합니다."
            return
        }
    }

    // 세션 생성 및 연결 토큰 얻기
    private fun getToken(sessionId: String) {
        // 첫 번째 단계: 세션 생성 요청
        val sessionUrl = "/api/sessions"
        val sessionBody = "{\"customSessionId\": \"$sessionId\"}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        httpClient?.httpCall(sessionUrl, "POST", "application/json", sessionBody, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 세션 생성 성공, 연결 토큰 요청 시작
                    val tokenUrl = "/api/sessions/$sessionId/connections"
                    val tokenBody = "{}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    httpClient?.httpCall(tokenUrl, "POST", "application/json", tokenBody, object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            response.use {
                                val responseBody = response.body?.string()
                                if (response.isSuccessful && responseBody != null) {
                                    getTokenSuccess(responseBody, sessionId)
                                } else {
                                    onError("서버 에러: ${response.message}")
                                }
                            }
                        }
                        override fun onFailure(call: Call, e: IOException) {
                            onError("네트워크 에러: ${e.message}")
                        }
                    })
                } else {
                    // 세션 생성 실패
                    onError("세션 생성 실패: ${response.message}")
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                onError("네트워크 에러: ${e.message}")
            }
        })
    }

    // 토큰으로 입장 성공
    private fun getTokenSuccess(token: String, sessionId: String) {
        _isConnected.value = true
        // WebSocket 연결 초기화
        startWebSocket()
    }

    // callback 구현
    private fun onSuccess() {
        participants.value = listOf(participantName.value) // 예시 데이터
    }

    override fun onConnected() {
        _isConnected.value = true
    }

    override fun onDisconnected() {
        _isConnected.value = false
        _errorMessage.value = "WebSocket Disconnected"
    }

    override fun onParticipantJoined(participant: String?) {
        participant?.let {
            participants.value += it
        }
    }

    override fun onParticipantLeft(participant: String?) {
        participant?.let {
            participants.value -= it
        }
    }

    // 웹소켓 시작
    private fun startWebSocket() {
        session?.let {
            it.setStreamObserver(this)
            val webSocket = CustomWebSocket(it, this)
            webSocket.execute()
            it.setWebSocket(webSocket)
        }
    }

    // 서버 url 연결 오류
    private fun connectionError(url: String?) {
        _errorMessage.value = "Error connecting to $url"
        _isConnected.value = false
    }


    // 미디어스트림 받기
    override fun onStreamReceived(stream: MediaStream, participant: RemoteParticipant) {
        val audioTrack = stream.audioTracks.firstOrNull()
        // 여기서 audioTrack를 사용한 오디오 처리 로직 구현
        // 예: audioTrack.enabled = true
    }

    // 세션 떠나기
    fun leaveSession() {
        if (session != null) {
            session!!.leaveSession()
        }
        if (httpClient != null) {
            httpClient!!.dispose()
        }
        _isConnected.value = false
    }



    override fun onError(message: String) {
        viewModelScope.launch {
            _errorMessage.value = message
            _isConnected.value = false
        }
    }
}
