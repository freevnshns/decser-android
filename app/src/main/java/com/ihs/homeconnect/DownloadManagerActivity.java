package com.ihs.homeconnect;

import android.app.Activity;
import android.os.Bundle;

import com.ihs.homeconnect.helpers.jsonrpcHandler;

public class DownloadManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        jsonrpcHandler jsonrpcHandler = new jsonrpcHandler(this);
        jsonrpcHandler.execute("aria2.tellActive");
    }
}
