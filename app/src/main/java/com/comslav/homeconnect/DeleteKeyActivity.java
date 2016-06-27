package com.comslav.homeconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.dbHandler;

import java.util.ArrayList;

public class DeleteKeyActivity extends Activity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_key);
        final Button connectButton = (Button) findViewById(R.id.bDeleteKey);
        final dbHandler dbInstance;
        dbInstance = new dbHandler(this, null);
        final ArrayList<String> peerNames = dbInstance.getNames();
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerHostDeleteSelector);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, peerNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> hostNames = dbInstance.getHostname();
                connectButton.setTag(R.id.SELECTED_HOSTNAME_SPINNER, hostNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostname = connectButton.getTag(R.id.SELECTED_HOSTNAME_SPINNER).toString();
                if (dbInstance.deleteContact(hostname))
                    Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Delete Failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
