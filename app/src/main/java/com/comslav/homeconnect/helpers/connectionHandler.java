package com.comslav.homeconnect.helpers;

import android.os.AsyncTask;
import android.os.Environment;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.util.Properties;

public class connectionHandler extends AsyncTask<Void, Void, Session> {

    public static String hostName;
    public static String userName;
    public static String keyName;
    public static Session session;


    public connectionHandler(String hostname, String username) throws JSchException {
        hostName = hostname;
        userName = username;
        JSch jsch = new JSch();
        session = jsch.getSession(userName, hostName, 22);
        if (userName.equals("user"))
            keyName = "self.ppk";
        else {
            keyName = hostname + ".ppk";
        }
        String Path = Environment.getExternalStorageDirectory().getPath() + "/comslav/" + keyName;
        jsch.addIdentity(Path);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        MyUserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);

    }

    public Session connect() throws JSchException {
        session.connect();
        return session;
    }

    @Override
    protected Session doInBackground(Void[] params) {
        try {
            return connect();
        } catch (JSchException e) {
            String logMessage = e.toString();
            loggingHandler loggingHandlerInstance = new loggingHandler();
            loggingHandlerInstance.addLog(logMessage);
            return null;
        }
    }

    public static class MyUserInfo implements UserInfo {
        @Override
        public String getPassphrase() {
            return "180793";
        }

        @Override
        public String getPassword() {
            return "180793";
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return true;
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
