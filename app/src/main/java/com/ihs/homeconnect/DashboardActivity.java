package com.ihs.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.dbHandler;
import com.ihs.homeconnect.helpers.loggingHandler;
import com.ihs.homeconnect.helpers.services;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.ArrayList;

public class DashboardActivity extends Activity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        TextView textView = (TextView) findViewById(R.id.tvLabel);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/alex/alex.ttf");
        textView.setTypeface(typeface);
        final dbHandler dbInstance;
        dbInstance = new dbHandler(this, null);
        final ArrayList<String> installedServices = dbInstance.getServices(1);
        ArrayAdapter<String> dashAdapter = new ArrayAdapter<>(this, R.layout.activity_dash, R.id.tvLabel, installedServices);
        final ListView listView = (ListView) findViewById(R.id.lvDashOptions);
        listView.setAdapter(dashAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (services ser : services.values()) {
                    if (ser.toString().equals(installedServices.get(position))) {
                        try {
                            session.setPortForwardingL(9000 + ser.port, "127.0.0.1", ser.port);
                            launchService(9000 + ser.port, ser.toString());
                        } catch (JSchException e) {
                            if (e.getMessage().startsWith("PortForwardingL"))
                                launchService(9000 + ser.port, ser.toString());
                            else {
                                loggingHandler loggingHandler = new loggingHandler();
                                loggingHandler.addLog(e.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    public void launchService(int port, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "http://127.0.0.1:" + port;
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.serverMediaDownload) {
            Intent intent = new Intent(this, DownloadMediaActivity.class);
            DownloadMediaActivity.session = session;
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (session.isConnected())
            session.disconnect();
        finish();
    }
}
