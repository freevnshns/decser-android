package com.ihs.homeconnect.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ihs.homeconnect.XmppActivity;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class xmppHandler extends AsyncTask<AbstractXMPPConnection, Void, AbstractXMPPConnection> {
    private ProgressDialog progressDialog;
    private Context mContext;

    public xmppHandler(Context mContext) {
        this.progressDialog = new ProgressDialog(mContext);
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog.setMessage("Connecting");
        this.progressDialog.show();
        this.progressDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    protected AbstractXMPPConnection doInBackground(AbstractXMPPConnection[] params) {
        try {
//            params[0].addAsyncStanzaListener(new StanzaListener() {
//                @Override
//                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//                    return;
//                }
//            }, filter);
            params[0].connect();
            params[0].login();
            return params[0];
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(AbstractXMPPConnection connection) {
        super.onPostExecute(connection);
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        XmppActivity.connection = connection;
        Intent intent = new Intent(mContext, XmppActivity.class);
        mContext.startActivity(intent);
    }
}
