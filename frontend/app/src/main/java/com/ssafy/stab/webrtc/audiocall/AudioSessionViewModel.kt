package com.ssafy.stab.webrtc.audiocall

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.stab.BuildConfig
import com.ssafy.stab.webrtc.utils.CustomHttpClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.Base64

class AudioSessionViewModel : ViewModel() {
    private val httpClient = CustomHttpClient(BuildConfig.OPENVIDU_URL)
    private val gson = Gson()

    private val _participants = MutableStateFlow<List<Connection>>(emptyList())
    val participants: StateFlow<List<Connection>> = _participants

    // 서버에 주기적으로 참여자 목록 데이터를 요청하는 기능
    private var fetchJob: Job? = null

    fun startFetchingSessionData(sessionId: String) {
        fetchJob?.cancel() // 기존 작업이 있으면 취소
        fetchJob = viewModelScope.launch {
            while (isActive) {
                getSessionConnection(sessionId)
                delay(15000) // 15초마다 데이터 가져오기
            }
        }
    }
    fun stopFetchingSessionData() {
        fetchJob?.cancel() // 데이터 가져오기 작업 취소
    }


    fun getSessionConnection(sessionId: String) {
        val url = "api/sessions/$sessionId/connection"
        val credentials = BuildConfig.OPENVIDU_SECRET
        val authorization = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())

        httpClient.httpGet(url, authorization, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    body?.let {
                        val connections = gson.fromJson(it, ConnectionsResponse::class.java)
                        viewModelScope.launch {
                            _participants.value = connections.content
                        }
                        // 이제 connections 객체를 사용하여 참가자 목록 등을 처리
                        connections.content.forEach { connection ->
                            Log.d("Connection Data", "ID: ${connection}")
                        }
                    }
                } else {
                    Log.e(
                        "HTTP Error",
                        "현재 세션에서 진행 중인 통화가 없습니다. ${response.code}"
                    )
                    if (response.code == 404) {
                        viewModelScope.launch {
                            _participants.value = emptyList() // 404 에러일 경우 참가자 목록을 초기화
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Network Error", "Failed to fetch session info: ${e.message}")
            }
        })
    }

}

data class ConnectionsResponse(
    val numberOfElements: Int,
    val content: List<Connection>
)

data class Connection(
    val id: String,
    val status: String,
    val sessionId: String,
    val serverData: String?,
    val clientData: String?,
    val role: String?
)
