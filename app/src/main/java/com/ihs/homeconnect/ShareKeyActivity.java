package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ihs.homeconnect.helpers.keyExchangeHandler;
import com.ihs.homeconnect.helpers.services;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ShareKeyActivity extends AppCompatActivity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_key);
        CheckBox cbDiscoverable = (CheckBox) findViewById(R.id.cbDiscoverable);
        assert cbDiscoverable != null;
        cbDiscoverable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        session.setPortForwardingL(services.HomeBase.port + 9000, "127.0.0.1", services.HomeBase.port);
                        new keyExchangeHandler(ShareKeyActivity.this).execute();
                    } catch (JSchException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
