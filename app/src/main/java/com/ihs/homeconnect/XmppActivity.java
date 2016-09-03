package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ihs.homeconnect.helpers.services;
import com.ihs.homeconnect.helpers.xmppHandler;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


public class XmppActivity extends AppCompatActivity {

    private AbstractXMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp);

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setUsernameAndPassword("sandi", "abcd");
        builder.setServiceName("sidzi.local");
        builder.setHost("192.168.0.201");
        builder.setPort(services.xmpp.port);
        connection = new XMPPTCPConnection(builder.build());
        xmppHandler xmppHandler = new xmppHandler();
        xmppHandler.execute(connection);
    }

    @Override
    public void onBackPressed() {
        connection.disconnect();
        super.onBackPressed();
    }
}
