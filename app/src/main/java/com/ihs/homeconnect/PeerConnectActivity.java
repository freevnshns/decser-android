package com.ihs.homeconnect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.koushikdutta.ion.Ion;

public class PeerConnectActivity extends Activity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            session.setPortForwardingL(9443, "127.0.0.1", 1443);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_peer_connect);
        ImageView imageView = (ImageView) findViewById(R.id.ivDP);
        Ion.with(this).load("https://127.0.0.1:9443/profilePicture_M").withBitmap().intoImageView(imageView);
//        TODO Replace this with in-built api handling
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String url = "https://127.0.0.1:9443";
//        intent.setData(Uri.parse(url));
//        startActivity(intent);

    }
}
