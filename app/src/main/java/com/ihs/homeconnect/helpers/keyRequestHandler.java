package com.ihs.homeconnect.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class keyRequestHandler extends AsyncTask<String, Void, Boolean> {
    public ProgressDialog progressDialog;
    private Context mContext;

    public keyRequestHandler(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Processing");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        if (s) {
            Toast.makeText(mContext, "Key received", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Socket socket = null;
        try {
            socket = new Socket(params[0], 42000);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.write("lol@lolmail.com");
            out.flush();
            String buffer;
            PrintWriter keyWriter = new PrintWriter(new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + params[0] + ".ppk", false));
            while ((buffer = in.readLine()) != null) {
                keyWriter.append(buffer).append('\n');
            }
            in.close();
            out.close();
            keyWriter.close();
        } catch (ConnectException e) {
            return Boolean.FALSE;
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } finally {
            try {
                assert socket != null;
                socket.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return Boolean.TRUE;
    }
}
