package com.ihs.homeconnect.helpers;

import android.os.AsyncTask;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class xmppHandler extends AsyncTask<AbstractXMPPConnection, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(AbstractXMPPConnection[] params) {
        try {
            params[0].connect();
            params[0].login();
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
        }
        return null;
    }
}
