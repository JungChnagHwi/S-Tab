package com.ssafy.stab.webrtc.openvidu;

import android.view.View;
import android.widget.TextView;

import org.webrtc.SurfaceViewRenderer;

<<<<<<< PATCH SET (56a786 feat: openvidu 음성통화만 되도록 커스텀한 코드 추가)
=======

>>>>>>> BASE      (1c4238 feat: 음성 통화방 참가자 목록 ui에 현재 세션 참가자가 표시되는 기능 구현)
public class RemoteParticipant extends Participant {

    private View view;
    private SurfaceViewRenderer videoView;
    private TextView participantNameText;

    public RemoteParticipant(String connectionId, String participantName, Session session) {
        super(connectionId, participantName, session);
        this.session.addRemoteParticipant(this);
    }

    public View getView() {
        return this.view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public SurfaceViewRenderer getVideoView() {
        return this.videoView;
    }

    public void setVideoView(SurfaceViewRenderer videoView) {
        this.videoView = videoView;
    }

    public TextView getParticipantNameText() {
        return this.participantNameText;
    }

    public void setParticipantNameText(TextView participantNameText) {
        this.participantNameText = participantNameText;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
