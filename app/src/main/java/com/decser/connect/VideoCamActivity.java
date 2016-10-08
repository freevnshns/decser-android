package com.decser.connect;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.decser.connect.helpers.services;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

public class VideoCamActivity extends AppCompatActivity {
    MjpegSurfaceView mjpegView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cam);
        mjpegView = (MjpegSurfaceView) findViewById(R.id.mjsv);
        int TIMEOUT = 10; //seconds
        Mjpeg.newInstance()
                .open("http://127.0.0.1:" + String.valueOf(services.vs.lport) + "/videocam", TIMEOUT)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(false);
                });
    }

    @Override
    public void onBackPressed() {
        try {
            if (mjpegView.isStreaming()) {
                mjpegView.stopPlayback();
            }
        } catch (NetworkOnMainThreadException e) {
//            TODO fix this
        }
        super.onBackPressed();
    }
}
