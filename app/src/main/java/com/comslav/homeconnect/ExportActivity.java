package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class ExportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        try
        {
            File selfKeyFile = new File(Environment.getExternalStorageDirectory().getPath() + "/comslav/self_public_share.ppk");
            sharingIntent.setType("*/*");
            sharingIntent.setPackage("com.android.bluetooth");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selfKeyFile));
            startActivity(Intent.createChooser(sharingIntent, "Share Key"));
        }catch (NullPointerException e){
            Toast.makeText(this.getBaseContext(), "Please import your Public Key first !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ImportKeyActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
