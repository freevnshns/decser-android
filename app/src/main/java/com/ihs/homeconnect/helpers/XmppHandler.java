package com.ihs.homeconnect.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ihs.homeconnect.DashboardActivity;
import com.ihs.homeconnect.XmppRosterActivity;
import com.ihs.homeconnect.XmppService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;


public class XmppHandler extends AsyncTask<Void, Void, AbstractXMPPConnection> {
    private ProgressDialog progressDialog;
    private Context mContext;

    public XmppHandler(Context mContext) {
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
    protected AbstractXMPPConnection doInBackground(Void[] params) {
        AbstractXMPPConnection connection;
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setHost("127.0.0.1");
        builder.setPort(services.xmpp.lport);
        String dev_service_name = DashboardActivity.connected_hostname.substring(0, DashboardActivity.connected_hostname.indexOf(".")) + ".local";
        builder.setServiceName("sinecos.local");//replace using bottom level domain name and .local
        connection = new XMPPTCPConnection(builder.build());
        try {
            connection.connect();
            XmppService.connection = connection;
            mContext.startService(new Intent(mContext, XmppService.class));
            return connection;
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
        Intent intent = new Intent(mContext, XmppRosterActivity.class);
        mContext.startActivity(intent);
    }
}
