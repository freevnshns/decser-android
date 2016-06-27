package com.comslav.homeconnect.helpers;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.util.Properties;

//TO BE USED ONLY IF CLIENT COMPLAINTS OF CONNECTION BREAK IN THE MIDDLE OF WORK
public class sshConnectionHandler extends IntentService {

    public sshConnectionHandler() {
        super("SSHConnection");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String accessLevel;
        String userName = intent.getStringExtra("username");
        String hostname = intent.getStringExtra("hostname");
        JSch jsch = new JSch();
        Session session = null;
        String keyName;
        if (userName.equals("user")) {
            keyName = "self.ppk";
            accessLevel = "user";
        } else {
            keyName = hostname + ".ppk";
            accessLevel = "limited-user";
        }
        try {
            session = jsch.getSession(userName, accessLevel, 22);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        String Path = Environment.getExternalStorageDirectory().getPath() + "/comslav/" + keyName;
        try {
            jsch.addIdentity(Path);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        assert session != null;
        session.setConfig(prop);
        MyUserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);
        try {
            session.connect(10000);
        } catch (JSchException e) {
            e.printStackTrace();
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
