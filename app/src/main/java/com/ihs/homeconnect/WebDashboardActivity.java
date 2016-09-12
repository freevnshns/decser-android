package com.ihs.homeconnect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebDashboardActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_dashboard);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        WebView wvVideoCam = (WebView) findViewById(R.id.wvWebDashboard);
        if (wvVideoCam != null) {
            wvVideoCam.setWebViewClient(new WebViewClient());
            wvVideoCam.getSettings().setDomStorageEnabled(true);
            wvVideoCam.getSettings().setJavaScriptEnabled(true);
            wvVideoCam.loadUrl("http://127.0.0.1:9080/");
        }
    }
}
