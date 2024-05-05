package com.ssafy.stab.webrtc.activities;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.runtime.Composable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.ssafy.stab.R;
import com.ssafy.stab.databinding.ActivityMainBinding;
import com.ssafy.stab.webrtc.fragments.PermissionsDialogFragment;
import com.ssafy.stab.webrtc.openvidu.LocalParticipant;
import com.ssafy.stab.webrtc.openvidu.RemoteParticipant;
import com.ssafy.stab.webrtc.openvidu.Session;
import com.ssafy.stab.webrtc.utils.CustomHttpClient;
import com.ssafy.stab.webrtc.websocket.CustomWebSocket;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;


import java.io.IOException;
import java.util.Random;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SessionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;
    private final String TAG = "SessionActivity";
    private ActivityMainBinding binding; // View Binding으로 생성된 바인딩 클래스


    LinearLayout views_container;
    Button start_finish_call;
    EditText session_name;
    EditText participant_name;
    EditText application_server_url;
    SurfaceViewRenderer localVideoView;
    TextView main_participant;
    FrameLayout peer_container;

    private String APPLICATION_SERVER_URL;
    private Session session;
    private CustomHttpClient httpClient;

    private EglBase rootEglBase = EglBase.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        askForPermissions();
        Random random = new Random();
        int randomIndex = random.nextInt(100);
        binding.participantName.setText(binding.participantName.getText().append(String.valueOf(randomIndex)));

        views_container = binding.viewsContainer;
        start_finish_call = binding.startFinishCall;
        session_name = binding.sessionName;
        participant_name = binding.participantName;
        application_server_url = binding.applicationServerUrl;
        localVideoView = binding.localGlSurfaceView;
        main_participant = binding.mainParticipant;
        peer_container = binding.peerContainer;
    }

    public void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    public void buttonPressed(View view) {
        if (start_finish_call.getText().equals(getResources().getString(R.string.hang_up))) {
            // Already connected to a session
            leaveSession();
            return;
        }
        if (arePermissionGranted()) {
            initViews();
            viewToConnectingState();

            APPLICATION_SERVER_URL = application_server_url.getText().toString();
            httpClient = new CustomHttpClient(APPLICATION_SERVER_URL);

            String sessionId = session_name.getText().toString();
            getToken(sessionId);
        } else {
            DialogFragment permissionsFragment = new PermissionsDialogFragment();
            permissionsFragment.show(getSupportFragmentManager(), "Permissions Fragment");
        }
    }

    private void getToken(String sessionId) {
        try {
            // Session Request
            RequestBody sessionBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"customSessionId\": \"" + sessionId + "\"}");
            this.httpClient.httpCall("/api/sessions", "POST", "application/json", sessionBody, new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d(TAG, "responseString: " + response.body().string());

                    // Token Request
                    RequestBody tokenBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{}");
                    httpClient.httpCall("/api/sessions/" + sessionId + "/connections", "POST", "application/json", tokenBody, new Callback() {

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            String responseString = null;
                            try {
                                responseString = response.body().string();
                            } catch (IOException e) {
                                Log.e(TAG, "Error getting body", e);
                            }
                            getTokenSuccess(responseString, sessionId);
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e(TAG, "Error POST /api/sessions/SESSION_ID/connections", e);
                            connectionError(APPLICATION_SERVER_URL);
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "Error POST /api/sessions", e);
                    connectionError(APPLICATION_SERVER_URL);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error getting token", e);
            e.printStackTrace();
            connectionError(APPLICATION_SERVER_URL);
        }
    }

    private void getTokenSuccess(String token, String sessionId) {
        // Initialize our session
        session = new Session(sessionId, token, views_container, this);

        // Initialize our local participant and start local camera
        String participantName = participant_name.getText().toString();
        LocalParticipant localParticipant = new LocalParticipant(participantName, session, this.getApplicationContext(), localVideoView);
        localParticipant.startCamera();
        runOnUiThread(() -> {
            // Update local participant view
            main_participant.setText(participant_name.getText().toString());
            main_participant.setPadding(20, 3, 20, 3);
        });

        // Initialize and connect the websocket to OpenVidu Server
        startWebSocket();
    }

    private void startWebSocket() {
        CustomWebSocket webSocket = new CustomWebSocket(session, this);
        webSocket.execute();
        session.setWebSocket(webSocket);
    }

    private void connectionError(String url) {
        Runnable myRunnable = () -> {
            Toast toast = Toast.makeText(this, "Error connecting to " + url, Toast.LENGTH_LONG);
            toast.show();
            viewToDisconnectedState();
        };
        new Handler(this.getMainLooper()).post(myRunnable);
    }

    private void initViews() {
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setMirror(true);
        localVideoView.setEnableHardwareScaler(true);
        localVideoView.setZOrderMediaOverlay(true);
    }

    public void viewToDisconnectedState() {
        runOnUiThread(() -> {
            localVideoView.clearImage();
            localVideoView.release();
            start_finish_call.setText(getResources().getString(R.string.start_button));
            start_finish_call.setEnabled(true);
            application_server_url.setEnabled(true);
            application_server_url.setFocusableInTouchMode(true);
            session_name.setEnabled(true);
            session_name.setFocusableInTouchMode(true);
            participant_name.setEnabled(true);
            participant_name.setFocusableInTouchMode(true);
            main_participant.setText(null);
            main_participant.setPadding(0, 0, 0, 0);
        });
    }

    public void viewToConnectingState() {
        runOnUiThread(() -> {
            start_finish_call.setEnabled(false);
            application_server_url.setEnabled(false);
            application_server_url.setFocusable(false);
            session_name.setEnabled(false);
            session_name.setFocusable(false);
            participant_name.setEnabled(false);
            participant_name.setFocusable(false);
        });
    }

    public void viewToConnectedState() {
        runOnUiThread(() -> {
            start_finish_call.setText(getResources().getString(R.string.hang_up));
            start_finish_call.setEnabled(true);
        });
    }

    public void createRemoteParticipantVideo(final RemoteParticipant remoteParticipant) {
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = () -> {
            View rowView = this.getLayoutInflater().inflate(R.layout.peer_video, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 20);
            rowView.setLayoutParams(lp);
            int rowId = View.generateViewId();
            rowView.setId(rowId);
            views_container.addView(rowView);
            SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
            remoteParticipant.setVideoView(videoView);
            videoView.setMirror(false);

            videoView.init(rootEglBase.getEglBaseContext(), null);
            videoView.setZOrderMediaOverlay(true);
            View textView = ((ViewGroup) rowView).getChildAt(1);
            remoteParticipant.setParticipantNameText((TextView) textView);
            remoteParticipant.setView(rowView);

            remoteParticipant.getParticipantNameText().setText(remoteParticipant.getParticipantName());
            remoteParticipant.getParticipantNameText().setPadding(20, 3, 20, 3);
        };
        mainHandler.post(myRunnable);
    }

    public void setRemoteMediaStream(MediaStream stream, final RemoteParticipant remoteParticipant) {
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        videoTrack.addSink(remoteParticipant.getVideoView());
        runOnUiThread(() -> {
            remoteParticipant.getVideoView().setVisibility(View.VISIBLE);
        });
    }

    public void leaveSession() {
        if(this.session != null) {
            this.session.leaveSession();
        }
        if(this.httpClient != null) {
            this.httpClient.dispose();
        }
        viewToDisconnectedState();
    }

    private boolean arePermissionGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
    }

    public EglBase getRootEglBase() {
        return rootEglBase;
    }

    @Override
    protected void onDestroy() {
        leaveSession();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        leaveSession();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        leaveSession();
        super.onStop();
    }

}
