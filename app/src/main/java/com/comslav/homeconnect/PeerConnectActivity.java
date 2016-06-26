package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class PeerConnectActivity extends Activity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            session.setPortForwardingL(9443, "127.0.0.1", 443);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "https://127.0.0.1:9443";
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
