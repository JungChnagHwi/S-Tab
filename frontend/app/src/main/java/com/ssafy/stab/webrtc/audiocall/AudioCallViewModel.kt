package com.ssafy.stab.webrtc.audiocall


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.BuildConfig
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.webrtc.openvidu.LocalParticipant
import com.ssafy.stab.webrtc.openvidu.RemoteParticipant
import com.ssafy.stab.webrtc.openvidu.Session
import com.ssafy.stab.webrtc.utils.CustomHttpClient
import com.ssafy.stab.webrtc.utils.PermissionManager
import com.ssafy.stab.webrtc.websocket.CustomWebSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.webrtc.MediaStream
import java.io.IOException
import java.util.Base64

// session 접속 데이터를 다루는 viewmodel
class AudioCallViewModel(application: Application) : AndroidViewModel(application), CustomWebSocket.WebSocketCallback, Session.StreamObserver {

    var sessionId = mutableStateOf("")
    var participantName = mutableStateOf("")
    private val serverUrl = BuildConfig.OPENVIDU_URL
    var participants = mutableStateOf(listOf<String>())
    private val _errorMessage = MutableStateFlow("")
    private var session: Session? = null
    private var httpClient: CustomHttpClient? = null
    var isMuted = mutableStateOf(false)
    var isSpeakerMuted = mutableStateOf(false)  // 스피커 음소거 상태를 저장하는 변수

    // 오디오 권한 체크 후, 세션 id와 서버 url이 유효한지 확인하고 session 입장 요청
    fun buttonPressed(context: Context) {
        viewModelScope.launch {
            val callState = PreferencesUtil.callState.first()  // 현재 상태를 한 번만 가져옵니다.
            val currentSessionId = sessionId.value
            Log.d("Debug", "Call State: is in call = ${callState.isInCall}, callSpaceId = ${callState.callSpaceId}")
            Log.d("Debug", "Current Session ID: $currentSessionId")

            if (callState.isInCall && callState.callSpaceId == currentSessionId) {
                leaveSession()
            } else if (callState.isInCall && callState.callSpaceId != currentSessionId) {
                leaveSession()
                startSession(context)
            } else if (!callState.isInCall) {
                startSession(context)
            }
        }
    }

    private fun startSession(context: Context) {
        val currentSessionId = sessionId.value
        httpClient = CustomHttpClient(serverUrl)
        getToken(currentSessionId)  // 세션 토큰 요청
    }

    // 세션 생성 및 연결 토큰 얻기
    private fun getToken(sessionId: String) {
        // 헤더 설정
        val credentials = BuildConfig.OPENVIDU_SECRET
        val authorization = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())
        // 첫 번째 단계: 세션 생성 요청
        val sessionUrl = "/api/sessions"
        val sessionBody = "{\"customSessionId\": \"$sessionId\"}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        httpClient?.httpCall(sessionUrl, "POST", "application/json", sessionBody, authorization, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful || response.code == 409) {
                    // 세션 생성 성공, 연결 토큰 요청 시작
                    val tokenUrl = "/api/sessions/$sessionId/connection"
                    val tokenBody = "{}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    httpClient?.httpCall(tokenUrl, "POST", "application/json", tokenBody, authorization, object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            response.use {
                                val responseBody = response.body?.string()
                                if (response.isSuccessful && responseBody != null) {

                                    getTokenSuccess(responseBody, sessionId)
                                } else {
                                    onError("서버 에러: ${response}")
                                }
                            }
                        }
                        override fun onFailure(call: Call, e: IOException) {
                            onError("네트워크 에러: ${e}")
                        }
                    })
                } else {
                    // 세션 생성 실패
                    onError("세션 생성 실패: ${response}")
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                onError("네트워크 에러: ${e}")
            }
        })
    }

    // 토큰으로 입장 성공
    private fun getTokenSuccess(token: String, sessionId: String) {
        PreferencesUtil.saveCallState(true, sessionId)
        // 현재 참가자 추가
        participants.value += participantName.value

        // 세션 초기화 및 로컬 참가자 오디오 시작
        val context = getApplication<Application>().applicationContext
        session = Session(sessionId, token, context) // context는 ViewModel이 아니므로 적절한 Context를 전달해야 합니다.
        session?.let {
            it.localParticipant = LocalParticipant(participantName.value, it, context) // LocalParticipant를 생성하고 세션에 설정
            it.localParticipant?.startAudio() // 오디오 시작
            it.setStreamObserver(this)
        }

        // WebSocket 연결 초기화
        startWebSocket()

    }

    override fun onConnected() {
        PreferencesUtil.saveCallState(true, sessionId.value)
    }

    override fun onDisconnected() {
        PreferencesUtil.saveCallState(false, null)
        _errorMessage.value = "WebSocket Disconnected"
    }

    // socket 이벤트로 실시간으로 참여자 정보 갱신하는 코드 -> 백엔드 서버에서도 정의 필요
    override fun onParticipantJoined(participant: String?) {
        participant?.let {
            participants.value += it
            Log.d("AudioCallViewModel", "Participant joined: $it")
        }
    }

    override fun onParticipantLeft(participant: String?) {
        participant?.let {
            participants.value -= it
            Log.d("AudioCallViewModel", "Participant left: $it")
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

    // 미디어스트림 받기
    override fun onStreamReceived(stream: MediaStream, participant: RemoteParticipant) {
        val audioTrack = stream.audioTracks.firstOrNull()
        // 실제 스피커 음량 조절 기능 구현 영역
    }
    // 마이크 토글 함수
    fun toggleMic() {
        isMuted.value = !isMuted.value
        session?.localParticipant?.muteMic(isMuted.value)
    }

    // 스피커 토글 함수
    fun toggleSpeaker() {
        isSpeakerMuted.value = !isSpeakerMuted.value
        // 실제 스피커 음량 조절 기능 구현 필요
    }

    // 세션 떠나기
    fun leaveSession() {
        if (session != null) {
            session!!.leaveSession()
            // 참가자 제거
            participants.value -= participantName.value
        }
        if (httpClient != null) {
            httpClient!!.dispose()
        }
        PreferencesUtil.saveCallState(false, null)
    }

    override fun onError(message: String) {
        viewModelScope.launch {
            _errorMessage.value = message
        }
    }
}
