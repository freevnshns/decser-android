package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.connectionHandler;
import com.comslav.homeconnect.helpers.dbHandler;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button connectButton = (Button) findViewById(R.id.bconnect);
        final dbHandler dbInstance;
        dbInstance = new dbHandler(this, null);
        final ArrayList<String> peerNames = dbInstance.getNames();
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerHostSelector);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, peerNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> hostNames = dbInstance.getHostname();
                connectButton.setTag(R.id.SELECTED_HOSTNAME_SPINNER, hostNames.get(position));
                if ("Me".equals(parent.getItemAtPosition(position))) {
                    connectButton.setTag(R.id.SELECTED_NAME_SPINNER, "user");
                } else {
                    connectButton.setTag(R.id.SELECTED_NAME_SPINNER, "limited-user");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostname = connectButton.getTag(R.id.SELECTED_HOSTNAME_SPINNER).toString();
                String accessLevel = connectButton.getTag(R.id.SELECTED_NAME_SPINNER).toString();
                try {
                    serverConnect(hostname, accessLevel);
                } catch (JSchException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void serverConnect(String hostname, String accessLevel) throws JSchException, ExecutionException, InterruptedException {
        connectionHandler connectionHandlerInstance = new connectionHandler(hostname, accessLevel);
        Session session = null;
        final AsyncTask execute = connectionHandlerInstance.execute();
        try {
            session = (Session) execute.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Context context = getApplicationContext();
        if (session != null) {
            Intent intent;
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            if (accessLevel.equals("user")) {
                DashboardActivity.session = session;
                intent = new Intent(this, DashboardActivity.class);
            } else {
                PeerConnectActivity.session = session;
                intent = new Intent(this, PeerConnectActivity.class);
            }
            startActivity(intent);
        } else
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export_key) {
            Intent intent = new Intent(this, ExportActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_import) {
            Intent intent = new Intent(this, ImportKeyActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_delete_key) {
            Intent intent = new Intent(this, DeleteKeyActivity.class);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
