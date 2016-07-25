package com.ihs.homeconnect.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ihs.homeconnect.ShareKeyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class keyExchangeHandler extends AsyncTask<String, Void, String> {
    private Context mContext;

    public keyExchangeHandler(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        ((ShareKeyActivity) mContext).working_lock = false;
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        try {
//            if (o.equals("")) {
//                builder.setTitle("No user connected during this period");
//            } else {
//                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.setCancelable(false);
//            }
//            AlertDialog promptAccess = builder.create();
//            JSONObject exchange_user = new JSONObject(o);
//            promptAccess.setTitle("Allow for " + exchange_user.get("user"));
//            promptAccess.show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        try {
            JSONObject status = new JSONObject(o);
            if (status.getBoolean("success")) {
                Toast.makeText(mContext, "Key Shared", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Somebody tried but his id wasn't correct", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String str = "";
        try {
            URL url = new URL("http://127.0.0.1:9080/doKeyExchangeWith" + params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(300000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = conn.getInputStream();
                try {
                    Reader reader;
                    reader = new InputStreamReader(stream, "UTF-8");
                    char[] buffer = new char[1000];
                    reader.read(buffer);
                    str = new String(buffer);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
