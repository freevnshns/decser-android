package com.ihs.homeconnect.helpers;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class jsonrpcHandler extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject response = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("jsonrpc", "2.0");
            jsonObject.accumulate("method", params[1]);
            if (params.length > 2) {
                JSONArray parameters = new JSONArray();
                JSONArray urls = new JSONArray();
                urls.put(params[2]);
                parameters.put(urls);
                jsonObject.accumulate("params", parameters);
            }
            jsonObject.accumulate("id", "aria2c");

            String endpoint = params[0];
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
