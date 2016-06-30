package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.dbHandler;
import com.comslav.homeconnect.helpers.loggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImportKeyActivity extends Activity {
    public static String importKeyPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_key);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
            requestPermissions(perms, 200);
        }
        final Context context = getApplicationContext();
        final String sdcard = Environment.getExternalStorageDirectory().getPath();
        Button keyChooser = (Button) findViewById(R.id.bKeyChooser);
        keyChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListFileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final Button importKey = (Button) findViewById(R.id.bKeyImporter);
        final EditText etPeerName = (EditText) findViewById(R.id.etPeerName);
        if (!selfKeyExists())
            etPeerName.setHint("Enter \"Me\" for your personal key");
        final EditText etHostname = (EditText) findViewById(R.id.etHostname);
        importKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream in;
                OutputStream out;
                try {
                    File dir = new File(sdcard + "/comslav/media/");
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
                        if (etHostname.getText().toString().equals(""))
                            Toast.makeText(context, "Please enter the hostname", Toast.LENGTH_SHORT).show();
                        else {
                            if (etPeerName.getText().toString().equals(""))
                                Toast.makeText(context, "Please enter a Name", Toast.LENGTH_SHORT).show();
                            else {
                                in = new FileInputStream(importKeyPath);
                                if (etPeerName.getText().toString().equals("Me") || etPeerName.getText().toString().equals("me"))
                                    out = new FileOutputStream(sdcard + "/comslav/self.ppk");
                                else
                                    out = new FileOutputStream(sdcard + "/comslav/" + etHostname.getText() + ".ppk");
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

    private boolean selfKeyExists() {
        dbHandler dbHandlerInstance = new dbHandler(this, null);
        for (String hostNames : dbHandlerInstance.getContactNameList()) {
            if (hostNames.equals("Me") || hostNames.equals("me"))
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_import_key, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
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
