package com.ihs.homeconnect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ihs.homeconnect.helpers.dbHandler;
import com.ihs.homeconnect.helpers.ormHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class XmppService extends Service {

    public static AbstractXMPPConnection connection;
    public static PacketCollector packetCollector;
    ormHelper ormHelper;
    private IBinder mBinder = new PacketBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            dbHandler dbHandler = new dbHandler(XmppService.this, null);

            String user_email = dbHandler.getUserEmail();
            connection.login(user_email.substring(0, user_email.lastIndexOf("@")), dbHandler.getUserPassword());
            ormHelper = OpenHelperManager.getHelper(this, com.ihs.homeconnect.helpers.ormHelper.class);
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (packetCollector.getCollectedCount() != 0) {
            addMessagesToDB();
        }
        packetCollector.cancel();
        connection.disconnect();
        OpenHelperManager.releaseHelper();
        super.onDestroy();
    }

    private void addMessagesToDB() {

    }

    public PacketCollector getPacketCollector() {
        return packetCollector;
    }

    public AbstractXMPPConnection getAbstractXMPPConnection() {
        return connection;
    }

    public class PacketBinder extends Binder {
        XmppService getService() {
            return XmppService.this;
        }
    }

}
