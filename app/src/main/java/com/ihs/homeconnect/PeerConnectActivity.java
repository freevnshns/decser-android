package com.ihs.homeconnect;

import android.app.Activity;
import android.os.Bundle;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class PeerConnectActivity extends Activity {
    public static Session session = null;
    public static String connected_hostname = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            session.setPortForwardingL(19000, "127.0.0.1", 10000);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_peer_connect);
    }

    @Override
    public void onBackPressed() {
        if (session.isConnected())
            session.disconnect();
        finish();
    }
}
