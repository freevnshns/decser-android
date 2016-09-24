package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ihs.homeconnect.helpers.services;

import org.json.JSONException;
import org.json.JSONObject;

public class PowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        final SwitchCompat scLight = (SwitchCompat) findViewById(R.id.scLight);
        assert scLight != null;
        RequestQueue requestQueue = Volley.newRequestQueue(PowerActivity.this);
        String url = "http://127.0.0.1:" + String.valueOf(services.power.lport) + "/powerControlget";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    scLight.setChecked((boolean) (new JSONObject(response).get("state")));
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
                        try {
                            if (!(boolean) (new JSONObject(response).get("state"))) {
                                Toast.makeText(PowerActivity.this, "Failed Sorry :(", Toast.LENGTH_SHORT).show();
                            }
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
            }
        });
    }
}
