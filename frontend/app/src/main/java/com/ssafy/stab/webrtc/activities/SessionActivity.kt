package com.ssafy.stab.webrtc.activities

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.ssafy.stab.R
import com.ssafy.stab.databinding.ActivityMainBinding
import com.ssafy.stab.webrtc.fragments.PermissionsDialogFragment
import com.ssafy.stab.webrtc.openvidu.LocalParticipant
import com.ssafy.stab.webrtc.openvidu.RemoteParticipant
import com.ssafy.stab.webrtc.openvidu.Session
import com.ssafy.stab.webrtc.utils.CustomHttpClient
import com.ssafy.stab.webrtc.websocket.CustomWebSocket
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.Response
import org.webrtc.EglBase
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer
import java.io.IOException
import java.util.Random

class SessionActivity : AppCompatActivity() {
    private val TAG = "SessionActivity"
    private var binding: ActivityMainBinding? = null // View Binding으로 생성된 바인딩 클래스
    var views_container: LinearLayout? = null
    var start_finish_call: Button? = null
    var session_name: EditText? = null
    var participant_name: EditText? = null
    var application_server_url: EditText? = null
    var localVideoView: SurfaceViewRenderer? = null
    var main_participant: TextView? = null
    var peer_container: FrameLayout? = null
    private var APPLICATION_SERVER_URL: String? = null
    private var session: Session? = null
    private var httpClient: CustomHttpClient? = null
    @JvmField
    val rootEglBase = EglBase.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        askForPermissions()
        val random = Random()
        val randomIndex = random.nextInt(100)
        binding!!.participantName.text =
            binding!!.participantName.getText().append(randomIndex.toString())
        views_container = binding!!.viewsContainer
        start_finish_call = binding!!.startFinishCall
        session_name = binding!!.sessionName
        participant_name = binding!!.participantName
        application_server_url = binding!!.applicationServerUrl
        localVideoView = binding!!.localGlSurfaceView
        main_participant = binding!!.mainParticipant
        peer_container = binding!!.peerContainer
    }

    fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        }
    }

    fun buttonPressed(view: View?) {
        if (start_finish_call!!.getText() == getResources().getString(R.string.hang_up)) {
            // Already connected to a session
            leaveSession()
            return
        }
        if (arePermissionGranted()) {
            initViews()
            viewToConnectingState()
            APPLICATION_SERVER_URL = application_server_url!!.getText().toString()
            httpClient = CustomHttpClient(APPLICATION_SERVER_URL)
            val sessionId = session_name!!.getText().toString()
            getToken(sessionId)
        } else {
            val permissionsFragment: DialogFragment = PermissionsDialogFragment()
            permissionsFragment.show(supportFragmentManager, "Permissions Fragment")
        }
    }

    private fun getToken(sessionId: String) {
        try {
            // Session Request
            val sessionBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                "{\"customSessionId\": \"$sessionId\"}"
            )
            httpClient!!.httpCall(
                "/api/sessions",
                "POST",
                "application/json",
                sessionBody,
                object : Callback {
                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "responseString: " + response.body!!.string())

                        // Token Request
                        val tokenBody =
                            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}")
                        httpClient!!.httpCall(
                            "/api/sessions/$sessionId/connections",
                            "POST",
                            "application/json",
                            tokenBody,
                            object : Callback {
                                override fun onResponse(call: Call, response: Response) {
                                    var responseString: String? = null
                                    try {
                                        responseString = response.body!!.string()
                                    } catch (e: IOException) {
                                        Log.e(TAG, "Error getting body", e)
                                    }
                                    getTokenSuccess(responseString, sessionId)
                                }

                                override fun onFailure(call: Call, e: IOException) {
                                    Log.e(TAG, "Error POST /api/sessions/SESSION_ID/connections", e)
                                    connectionError(APPLICATION_SERVER_URL)
                                }
                            })
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "Error POST /api/sessions", e)
                        connectionError(APPLICATION_SERVER_URL)
                    }
                })
        } catch (e: IOException) {
            Log.e(TAG, "Error getting token", e)
            e.printStackTrace()
            connectionError(APPLICATION_SERVER_URL)
        }
    }

    private fun getTokenSuccess(token: String?, sessionId: String) {
        // Initialize our session
        session = Session(sessionId, token, views_container, this)

        // Initialize our local participant and start local camera
        val participantName = participant_name!!.getText().toString()
        val localParticipant =
            LocalParticipant(participantName, session, this.applicationContext, localVideoView)
        localParticipant.startCamera()
        runOnUiThread {

            // Update local participant view
            main_participant!!.text = participant_name!!.getText().toString()
            main_participant!!.setPadding(20, 3, 20, 3)
        }

        // Initialize and connect the websocket to OpenVidu Server
        startWebSocket()
    }

    private fun startWebSocket() {
        val webSocket = CustomWebSocket(session, this)
        webSocket.execute()
        session!!.setWebSocket(webSocket)
    }

    private fun connectionError(url: String?) {
        val myRunnable = Runnable {
            val toast = Toast.makeText(this, "Error connecting to $url", Toast.LENGTH_LONG)
            toast.show()
            viewToDisconnectedState()
        }
        Handler(this.mainLooper).post(myRunnable)
    }

    private fun initViews() {
        localVideoView!!.init(rootEglBase.eglBaseContext, null)
        localVideoView!!.setMirror(true)
        localVideoView!!.setEnableHardwareScaler(true)
        localVideoView!!.setZOrderMediaOverlay(true)
    }

    fun viewToDisconnectedState() {
        runOnUiThread {
            localVideoView!!.clearImage()
            localVideoView!!.release()
            start_finish_call!!.text = getResources().getString(R.string.start_button)
            start_finish_call!!.setEnabled(true)
            application_server_url!!.setEnabled(true)
            application_server_url!!.setFocusableInTouchMode(true)
            session_name!!.setEnabled(true)
            session_name!!.setFocusableInTouchMode(true)
            participant_name!!.setEnabled(true)
            participant_name!!.setFocusableInTouchMode(true)
            main_participant!!.setText("")
            main_participant!!.setPadding(0, 0, 0, 0)
        }
    }

    fun viewToConnectingState() {
        runOnUiThread {
            start_finish_call!!.setEnabled(false)
            application_server_url!!.setEnabled(false)
            application_server_url!!.isFocusable = false
            session_name!!.setEnabled(false)
            session_name!!.isFocusable = false
            participant_name!!.setEnabled(false)
            participant_name!!.isFocusable = false
        }
    }

    fun viewToConnectedState() {
        runOnUiThread {
            start_finish_call!!.text = getResources().getString(R.string.hang_up)
            start_finish_call!!.setEnabled(true)
        }
    }

    fun createRemoteParticipantVideo(remoteParticipant: RemoteParticipant) {
        val mainHandler = Handler(this.mainLooper)
        val myRunnable = Runnable {
            val rowView = this.layoutInflater.inflate(R.layout.peer_video, null)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 20)
            rowView.setLayoutParams(lp)
            val rowId = View.generateViewId()
            rowView.setId(rowId)
            views_container!!.addView(rowView)
            val videoView = (rowView as ViewGroup).getChildAt(0) as SurfaceViewRenderer
            remoteParticipant.videoView = videoView
            videoView.setMirror(false)
            videoView.init(rootEglBase.eglBaseContext, null)
            videoView.setZOrderMediaOverlay(true)
            val textView = rowView.getChildAt(1)
            remoteParticipant.participantNameText = textView as TextView
            remoteParticipant.view = rowView
            remoteParticipant.participantNameText.text = remoteParticipant.participantName
            remoteParticipant.participantNameText.setPadding(20, 3, 20, 3)
        }
        mainHandler.post(myRunnable)
    }

    fun setRemoteMediaStream(stream: MediaStream, remoteParticipant: RemoteParticipant) {
        val videoTrack = stream.videoTracks[0]
        videoTrack.addSink(remoteParticipant.videoView)
        runOnUiThread { remoteParticipant.videoView.setVisibility(View.VISIBLE) }
    }

    fun leaveSession() {
        if (session != null) {
            session!!.leaveSession()
        }
        if (httpClient != null) {
            httpClient!!.dispose()
        }
        viewToDisconnectedState()
    }

    private fun arePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_DENIED
    }

    override fun onDestroy() {
        leaveSession()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        leaveSession()
        super.onBackPressed()
    }

    override fun onStop() {
        leaveSession()
        super.onStop()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
    }
}
