package com.ihs.homeconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.jsonrpcHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadManagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        jsonrpcHandler jsonrpcHandler = new jsonrpcHandler(this);
        jsonrpcHandler.execute("aria2.tellActive");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JSONObject rpc_result;
                try {
                    rpc_result = new JSONObject(intent.getStringExtra("rpc_result"));
                    Toast.makeText(context, rpc_result.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter("com.ihs.homeconnect.dm.ARIA_UPDATE"));
        FloatingActionButton fabAddNewUri = (FloatingActionButton) findViewById(R.id.fabAddDownloadUri);
        assert fabAddNewUri != null;
        fabAddNewUri.setImageResource(R.drawable.ic_add_contact);
        fabAddNewUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadManagerActivity.this);
                builder.setTitle("Add a download");
                final EditText input_url = new EditText(getApplicationContext());
                input_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input_url.setTextColor(Color.BLACK);
                builder.setView(input_url);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsonrpcHandler jsonrpcHandler = new jsonrpcHandler(DownloadManagerActivity.this);
                        jsonrpcHandler.execute("aria2.addUri", input_url.getText().toString());
                    }
                });
                builder.show();
            }
        });
    }
}
