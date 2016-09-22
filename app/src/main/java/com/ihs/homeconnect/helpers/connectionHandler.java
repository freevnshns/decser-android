package com.ihs.homeconnect.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.ihs.homeconnect.DashboardActivity;
import com.ihs.homeconnect.PeerConnectActivity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.util.Properties;

public class connectionHandler extends AsyncTask<Void, Void, Session> {

    public static Session session;
    private static String hostName;
    private static String userName;
    private ProgressDialog progressDialog;
    private Context mContext;


    public connectionHandler(String hostname, String username, Context mContext) throws JSchException {
        this.mContext = mContext;
        this.progressDialog = new ProgressDialog(mContext);
        hostName = hostname;
        userName = username;
        JSch jsch = new JSch();
        session = jsch.getSession(userName, hostName, 3933);
        String keyName;
        if (userName.equals("user"))
            keyName = "self.ppk";
        else {
            keyName = hostname + ".ppk";
        }
        String Path = Environment.getExternalStorageDirectory().getPath() + "/ihs/" + keyName;
        jsch.addIdentity(Path);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        MyUserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        session = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog.setMessage("Connecting");
        this.progressDialog.show();
        this.progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(Session session) {
        super.onPostExecute(session);
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        if (session != null) {
            Intent intent;
            if (userName.equals("user")) {
                DashboardActivity.session = session;
                DashboardActivity.connected_hostname = hostName;
                intent = new Intent(mContext, DashboardActivity.class);
            } else {
                PeerConnectActivity.session = session;
                PeerConnectActivity.connected_hostname = hostName;
                intent = new Intent(mContext, PeerConnectActivity.class);
            }
            Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Session doInBackground(Void[] params) {
        try {
            session.connect();
            return session;
        } catch (JSchException e) {
            String logMessage = e.toString();
            loggingHandler loggingHandlerInstance = new loggingHandler();
            loggingHandlerInstance.addLog(logMessage);
            return null;
        }

    }

    private static class MyUserInfo implements UserInfo {
        @Override
        public String getPassphrase() {
            return "";
        }

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            return false;
        }

        @Override
        public void showMessage(String s) {

        }
    }
}
