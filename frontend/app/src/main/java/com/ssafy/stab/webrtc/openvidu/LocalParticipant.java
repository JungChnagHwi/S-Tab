package com.ssafy.stab.webrtc.openvidu;

import android.content.Context;


import org.webrtc.AudioSource;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;


import java.util.ArrayList;
import java.util.Collection;

public class LocalParticipant extends Participant {

    private Context context;
    private SurfaceTextureHelper surfaceTextureHelper;
    private VideoCapturer videoCapturer;

    private Collection<IceCandidate> localIceCandidates;
    private SessionDescription localSessionDescription;

    public LocalParticipant(String participantName, Session session, Context context) {
        super(participantName, session);
        this.context = context;
        this.localIceCandidates = new ArrayList<>();
        session.setLocalParticipant(this);
    }

    public void startAudio() {
        PeerConnectionFactory peerConnectionFactory = this.session.getPeerConnectionFactory();

        // Create AudioSource
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        this.audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        // Add the audio track to the peer connection
        if (this.peerConnection != null) {
            this.peerConnection.addTrack(this.audioTrack);
        }
    }

    public void muteMic(boolean mute) {
        if (audioTrack != null) {
            audioTrack.setEnabled(!mute);
        }
    }

    public void storeIceCandidate(IceCandidate iceCandidate) {
        localIceCandidates.add(iceCandidate);
    }

    public Collection<IceCandidate> getLocalIceCandidates() {
        return this.localIceCandidates;
    }

    public void storeLocalSessionDescription(SessionDescription sessionDescription) {
        localSessionDescription = sessionDescription;
    }

    public SessionDescription getLocalSessionDescription() {
        return this.localSessionDescription;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;
        }
    }

}
