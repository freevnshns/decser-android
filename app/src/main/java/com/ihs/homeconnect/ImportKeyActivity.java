package com.ihs.homeconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.dbHandler;
import com.ihs.homeconnect.helpers.loggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImportKeyActivity extends AppCompatActivity {
    public static String importKeyPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_key);
        final Context context = getApplicationContext();
        final String sdcard = Environment.getExternalStorageDirectory().getPath() + "/ihs/";
        final EditText etPeerName = (EditText) findViewById(R.id.etPeerName);
        final RadioGroup isPersonalKey = (RadioGroup) findViewById(R.id.rgIsPersonalKey);
        assert isPersonalKey != null;
        isPersonalKey.check(R.id.rbGuestKey);
        final EditText etHostname = (EditText) findViewById(R.id.etHostname);
        final Button importKey = (Button) findViewById(R.id.bKeyImporter);
        assert importKey != null;
        importKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream in;
                OutputStream out;
                try {
                    File dir = new File(sdcard + "server_downloads/");
                    if (!dir.exists()) {
                        //noinspection StatementWithEmptyBody
                        if (dir.mkdirs()) {
                            //Directory Creation Success
                        } else {
                            //Directory Creation Failure
                        }
                    }
                    if (importKeyPath == null)
                        Toast.makeText(context, "Please select a key file", Toast.LENGTH_SHORT).show();
                    else {
                        assert etHostname != null;
                        if (etHostname.getText().toString().equals(""))
                            Toast.makeText(context, "Please enter the hostname", Toast.LENGTH_SHORT).show();
                        else {
                            assert etPeerName != null;
                            if (etPeerName.getText().toString().equals(""))
                                Toast.makeText(context, "Please enter a Name", Toast.LENGTH_SHORT).show();
                            else {
                                in = new FileInputStream(importKeyPath);
                                if (isPersonalKey.getCheckedRadioButtonId() == R.id.rbPersonalKey)
                                    out = new FileOutputStream(sdcard + "self.ppk");
                                else
                                    out = new FileOutputStream(sdcard + etHostname.getText() + ".ppk");
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, read);
                                }
                                in.close();
                                out.flush();
                                out.close();
                                final dbHandler dbInstance;
                                dbInstance = new dbHandler(context, null);
                                dbInstance.addContact(etPeerName.getText().toString(), etHostname.getText().toString());
                                Toast.makeText(context, "Successfully Imported", Toast.LENGTH_SHORT).show();
                                importKeyPath = null;
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Import Failed", Toast.LENGTH_SHORT).show();
                    loggingHandler loggingHandler = new loggingHandler();
                    loggingHandler.addLog(e.getMessage());
                    loggingHandler.addLog(importKeyPath);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
