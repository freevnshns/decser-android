package com.ihs.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class ExportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        try {
            File selfKeyFile = new File(Environment.getExternalStorageDirectory().getPath() + "/comslav/self_public_share.ppk");
            sharingIntent.setType("*/*");
            sharingIntent.setPackage("com.android.bluetooth");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selfKeyFile));
            startActivity(Intent.createChooser(sharingIntent, "Share Key"));
        } catch (NullPointerException e) {
            Toast.makeText(this.getBaseContext(), "Please import your Public Key first !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ImportKeyActivity.class);
            startActivity(intent);
        }
    }
}
