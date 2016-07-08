package com.ihs.homeconnect.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class jsonrpcHandler extends AsyncTask<String, Void, JSONObject> {

    private Context mContext;

    public jsonrpcHandler(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            Toast.makeText(mContext, result.getString("jsonrpc"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject response = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("jsonrpc", "2.0");
            for (String param : params) {
                jsonObject.accumulate("method", param);
            }
            jsonObject.accumulate("id", "aria2c");
            String endpoint = "http://192.168.1.200:6800/jsonrpc";
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(endpoint)).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.flush();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder stringResponse = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String responseLine;
                while ((responseLine = bufferedReader.readLine()) != null) {
                    stringResponse.append(responseLine);
                }
                bufferedReader.close();
                response = new JSONObject(String.valueOf(stringResponse));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
