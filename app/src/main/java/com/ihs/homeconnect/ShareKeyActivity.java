package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ihs.homeconnect.helpers.keyExchangeHandler;
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
                        session.setPortForwardingR(21000, "127.0.0.1", 21000);
                        new keyExchangeHandler(ShareKeyActivity.this).execute();
                    } catch (JSchException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
