package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ihs.homeconnect.helpers.keyRequestHandler;

public class RequestKeyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_key);
        final EditText etRequestHostname = (EditText) findViewById(R.id.etRequestHostname);
        Button bRequestKey = (Button) findViewById(R.id.bRequestKey);
        assert bRequestKey != null;
        assert etRequestHostname != null;
        bRequestKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new keyRequestHandler(RequestKeyActivity.this).execute(etRequestHostname.getText().toString());
            }
        });
    }
}