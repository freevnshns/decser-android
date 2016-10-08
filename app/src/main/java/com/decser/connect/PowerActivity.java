package com.decser.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.decser.connect.helpers.services;

import org.json.JSONException;
import org.json.JSONObject;

public class PowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
//        TODO make this modular i.e. fetches list of switches controlled and shows views accordingly
        final SwitchCompat scLight = (SwitchCompat) findViewById(R.id.scLight);
        assert scLight != null;
        RequestQueue requestQueue = Volley.newRequestQueue(PowerActivity.this);
        String url = "http://127.0.0.1:" + String.valueOf(services.power.lport) + "/powerControlget";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if ((int) (new JSONObject(response).get("state")) == 1)
                        scLight.setChecked(true);
                    else
                        scLight.setChecked(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);

        scLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RequestQueue requestQueue = Volley.newRequestQueue(PowerActivity.this);
                String url = "http://127.0.0.1:" + String.valueOf(services.power.lport) + "/powerControl";
                if (isChecked) {
                    url += "on";
                } else {
                    url += "off";
                }
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                requestQueue.add(request);
            }
        });
    }
}
