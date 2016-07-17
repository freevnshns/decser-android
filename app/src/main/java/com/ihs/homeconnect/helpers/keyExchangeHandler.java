package com.ihs.homeconnect.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class keyExchangeHandler extends AsyncTask<Void, Void, Void> {
    private ServerSocket serverSocket;
    private Context mContext;

    public keyExchangeHandler(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            int commPort = 21000;
            serverSocket = new ServerSocket(commPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog promptAccess = builder.create();
        String requestId = "";
        promptAccess.setTitle("Allow for " + requestId);
        promptAccess.show();
//        Process rest of the formatlitire here
    }

    @Override
    protected void onCancelled(Void o) {
        super.onCancelled(o);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void[] params) {
        Socket socket;
        BufferedReader bufferedReader;
        try {
            socket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
