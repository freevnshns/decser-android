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

import com.ihs.homeconnect.helpers.DbHandler;
import com.ihs.homeconnect.helpers.loggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AddContactActivity extends AppCompatActivity {
    public static String importKeyPath = null;
    private int REQUEST_KEY_PATH = 300;

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
        final Button selectKey = (Button) findViewById(R.id.bSelectKey);
        if (selectKey != null)
            selectKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddContactActivity.this, ListFileActivity.class);
                    startActivityForResult(intent, REQUEST_KEY_PATH);
                }
            });
        final Button importKey = (Button) findViewById(R.id.bKeyImporter);

        if (importKey != null)
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
                        String access_lvl = "limited-user";
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
                                    if (isPersonalKey.getCheckedRadioButtonId() == R.id.rbPersonalKey) {
                                        access_lvl = "user";
                                        out = new FileOutputStream(sdcard + "self.ppk");
                                    } else
                                        out = new FileOutputStream(sdcard + etHostname.getText() + ".ppk");
                                    byte[] buffer = new byte[1024];
                                    int read;
                                    while ((read = in.read(buffer)) != -1) {
                                        out.write(buffer, 0, read);
                                    }
                                    in.close();
                                    out.flush();
                                    out.close();
                                    final DbHandler dbInstance;
                                    dbInstance = new DbHandler(context, null);
                                    dbInstance.addContact(etPeerName.getText().toString(), etHostname.getText().toString(), access_lvl);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_KEY_PATH && resultCode == RESULT_OK) {
            String path;
            path = data.getStringExtra("filepath");
            if (!path.equals(""))
                importKeyPath = path;
            else
                startActivityForResult(new Intent(AddContactActivity.this, ListFileActivity.class), REQUEST_KEY_PATH);
        }
    }
}
