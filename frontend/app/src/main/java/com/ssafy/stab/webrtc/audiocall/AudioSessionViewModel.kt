package com.ssafy.stab.webrtc.audiocall

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ssafy.stab.BuildConfig
import com.ssafy.stab.webrtc.utils.CustomHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.Base64

class AudioSessionViewModel : ViewModel() {
    private val httpClient = CustomHttpClient(BuildConfig.OPENVIDU_URL)
    private val gson = Gson()

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
                        // 이제 connections 객체를 사용하여 참가자 목록 등을 처리
                        connections.content.forEach { connection ->
                            Log.d("Connection Data", "ID: ${connection}")
                        }
                    }
                } else {
                    Log.e("HTTP Error", "Failed to fetch session info, Status code: ${response.code}, Message: ${response}")
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
    val record: Boolean?,
    val role: String?
)
