package com.ihs.homeconnect.helpers;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class keyRequestHandler extends AsyncTask<String, Void, String> {
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Socket socket = new Socket(params[0], 42000);
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String buffer;
            PrintWriter keyWriter = new PrintWriter(new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + params[0] + ".ppk", false));
            while ((buffer = in.readLine()) != null) {
                keyWriter.append(buffer);
            }
            keyWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
