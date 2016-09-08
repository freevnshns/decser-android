package com.ihs.homeconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ihs.homeconnect.helpers.dbHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            requestPermissions(perms, 200);
        }
        final EditText etRegPass = (EditText) findViewById(R.id.etRegPass);
        final EditText etRegEmailID = (EditText) findViewById(R.id.etRegEmailID);
        final EditText etRegHostname = (EditText) findViewById(R.id.etRegHostname);
        Button bRegister = (Button) findViewById(R.id.bRegister);
        if (bRegister != null && etRegEmailID != null && etRegHostname != null && etRegEmailID.getText().toString().equals("") && etRegHostname.getText().toString().equals(""))
            bRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
                    try {
                        String registrationUrl = "http://192.168.1.101:5000/register";
                        final JSONObject postRequest = new JSONObject("{'user_email':'" + etRegEmailID.getText().toString() + "','user_hostname':'" + etRegHostname.getText().toString() + "'}");
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, registrationUrl, postRequest, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.print(response.toString());
                                try {
                                    if (response.getBoolean("registration")) {
                                        dbHandler dbInstance = new dbHandler(RegistrationActivity.this, null);
                                        if (dbInstance.insertUserDetails(postRequest.getString("user_email"), postRequest.getString("user_hostname"), 1, etRegPass.getText().toString())) {
                                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        queue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
    }
}
