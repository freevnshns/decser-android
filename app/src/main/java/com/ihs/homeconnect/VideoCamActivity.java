package com.ihs.homeconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ihs.homeconnect.helpers.services;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class VideoCamActivity extends AppCompatActivity {
    public static Session session = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cam);
        try {
            session.setPortForwardingL(services.VideoSurveillance.port, "127.0.0.1", services.VideoSurveillance.port);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://127.0.0.1:" + String.valueOf(services.VideoSurveillance.port) + "/"));
            startActivity(i);
        } catch (JSchException e) {
            e.printStackTrace();
        }
//        WebView wvVideoCam = (WebView) findViewById(R.id.wvVideoCam);
//        if (wvVideoCam != null) {
//            wvVideoCam.setWebViewClient(new WebViewClient());
//            wvVideoCam.getSettings().setDomStorageEnabled(true);
//            wvVideoCam.loadUrl("http://192.168.0.201:8081/");
//        }
    }
}
