package com.decser.connect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.decser.connect.entities.ChatMessageModel;
import com.decser.connect.entities.RosterModel;
import com.decser.connect.helpers.DbHandler;
import com.decser.connect.helpers.OrmHelper;
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
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class XmppService extends Service {

    public static AbstractXMPPConnection connection;
    ArrayList<RosterModel> rosterModels = new ArrayList<>();
    private PacketCollector packetCollector;
    private IBinder mBinder = new PacketBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            OrmHelper ormHelper;
            ormHelper = OpenHelperManager.getHelper(this, OrmHelper.class);
            DbHandler dbHandler = new DbHandler(XmppService.this, null);

            StanzaFilter filter = new StanzaTypeFilter(Message.class);
            if (connection != null && connection.isConnected()) {
                packetCollector = connection.createPacketCollector(filter);
                String user_email = dbHandler.getUserEmail();
                connection.login(user_email.substring(0, user_email.lastIndexOf("@")), dbHandler.getUserPassword());

                try {
                    Roster roster = Roster.getInstanceFor(connection);
                    Dao<ChatMessageModel, Integer> chatDao = ormHelper.getDao(ChatMessageModel.class);
                    int offline_message_count = packetCollector.getCollectedCount();
                    while (offline_message_count > 0) {
                        Stanza offlineMessage = packetCollector.pollResult();
                        String sender = offlineMessage.getFrom().substring(0, offlineMessage.getFrom().lastIndexOf("/"));
                        chatDao.create(new ChatMessageModel(sender, ((Message) offlineMessage).getBody(), true, false));
                        offline_message_count--;
                    }
                    for (RosterEntry entry : roster.getEntries()) {
                        rosterModels.add(new RosterModel(entry.getUser(), entry.getName(), false));
                    }
                    packetCollector.cancel();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        connection.disconnect();
        OpenHelperManager.releaseHelper();
        super.onDestroy();
    }

    public AbstractXMPPConnection getAbstractXMPPConnection() {
        return connection;
    }

    public ArrayList<RosterModel> getRosterModels() {
        return rosterModels;
    }

    public class PacketBinder extends Binder {
        XmppService getService() {
            return XmppService.this;
        }
    }

}
