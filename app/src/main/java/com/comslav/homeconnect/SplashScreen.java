package com.comslav.homeconnect;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreen extends Activity {
    private final int SPLASH_DISPLAY_LENGTH;

    public SplashScreen() {
        SPLASH_DISPLAY_LENGTH = 2500;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_splash_screen);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ActionBar actionBar = getActionBar();
                    assert actionBar != null;
                    actionBar.hide();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
