package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.keyExchangeHandler;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ShareKeyActivity extends AppCompatActivity {
    public static Session session = null;
    public boolean working_lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_key);
        try {
            session.setPortForwardingL(9080, "127.0.0.1", 80);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        final EditText etEmailID = (EditText) findViewById(R.id.etEmailID);
        Button bDiscoverable = (Button) findViewById(R.id.bDiscoverable);
        assert bDiscoverable != null;
        bDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!working_lock) {
                    assert etEmailID != null;
                    String emailID = etEmailID.getText().toString();
                    if (emailID.equals("")) {
                        Toast.makeText(ShareKeyActivity.this, "Please enter an email id", Toast.LENGTH_SHORT).show();
                    } else {
                        working_lock = true;
                        new keyExchangeHandler(ShareKeyActivity.this).execute(emailID);
                    }
                } else {
                    Toast.makeText(ShareKeyActivity.this, "Please wait for the first share to finish", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
