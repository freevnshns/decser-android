package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.dbHandler;
import com.comslav.homeconnect.helpers.loggingHandler;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.ArrayList;

public class InstallServicesActivity extends Activity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_services);
        final dbHandler dbInstance;
        dbInstance = new dbHandler(this, null);
        final ArrayList<String> availableServices = dbInstance.getServices(0);
        ArrayAdapter<String> dashAdapter = new ArrayAdapter<>(this, R.layout.activity_dash, R.id.tvLabel, availableServices);
        final ListView listView = (ListView) findViewById(R.id.lvInstallServicesList);
        listView.setAdapter(dashAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem = parent.getItemAtPosition(position);
                try {
                    installService(selectedItem.toString());
                    Toast.makeText(getApplicationContext(), "Service " + selectedItem.toString() + " successfully installed", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    loggingHandler loggingHandler = new loggingHandler();
                    loggingHandler.addLog(e.getMessage());
                }
            }
        });
    }

    public void installService(String selectedService) throws JSchException {
//        Implement real installation
        dbHandler dbInstance;
        dbInstance = new dbHandler(this,null);
        dbInstance.enableService(selectedService);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
