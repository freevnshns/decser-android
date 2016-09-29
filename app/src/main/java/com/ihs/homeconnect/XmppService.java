package com.ihs.homeconnect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ihs.homeconnect.helpers.DbHandler;
import com.ihs.homeconnect.helpers.OrmHelper;
import com.ihs.homeconnect.models.ChatMessage;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import java.io.IOException;
import java.sql.SQLException;

public class XmppService extends Service {

    public static AbstractXMPPConnection connection;
    private PacketCollector packetCollector;
    private OrmHelper ormHelper = OpenHelperManager.getHelper(this, OrmHelper.class);

    private IBinder mBinder = new PacketBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            DbHandler dbHandler = new DbHandler(XmppService.this, null);

            StanzaFilter filter = new StanzaTypeFilter(Message.class);
            packetCollector = connection.createPacketCollector(filter);
            String user_email = dbHandler.getUserEmail();
            connection.login(user_email.substring(0, user_email.lastIndexOf("@")), dbHandler.getUserPassword());

            addMessagesToDB();
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

    public void addMessagesToDB() {
        int offline_message_count = packetCollector.getCollectedCount();
        try {
            Dao<ChatMessage, Integer> chatDao = ormHelper.getDao(ChatMessage.class);
            while (offline_message_count > 0) {
                Stanza offlineMessage = packetCollector.pollResult();
                String sender = offlineMessage.getFrom().substring(0, offlineMessage.getFrom().lastIndexOf("/"));
                chatDao.create(new ChatMessage(sender, ((Message) offlineMessage).getBody(), true, false));
                offline_message_count--;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
