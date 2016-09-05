package com.ihs.homeconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.verticalSpaceDecorationHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;

public class XmppActivity extends AppCompatActivity {

    public static AbstractXMPPConnection connection;
    public static PacketCollector packetCollector;
    public ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp);
        chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                if (!createdLocally) {
                    XmppChatActivity.chat = chat;
                    Intent intent = new Intent(XmppActivity.this, XmppChatActivity.class);
                    startActivity(intent);
                }
            }
        });
        packetCollector.cancel();
        int offline_message_count = packetCollector.getCollectedCount();
        ArrayList<rosterEntry> roster_sender = new ArrayList<>();
        while (offline_message_count > 0) {
            Stanza offlineMessage = packetCollector.pollResult();
            String sender = offlineMessage.getFrom().substring(0, offlineMessage.getFrom().lastIndexOf("/"));
            if (!roster_sender.contains(new rosterEntry(sender, true)))
                roster_sender.add(new rosterEntry(sender, true));
//            ((Message)offlineMessage).getBody(); Add this to database
            offline_message_count--;
        }

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvXmppContacts);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        Roster roster = Roster.getInstanceFor(connection);


        for (RosterEntry entry : roster.getEntries()) {
            if (!((roster_sender.contains(new rosterEntry(entry.getUser(), false))) && roster_sender.contains(new rosterEntry(entry.getUser(), true))))
                roster_sender.add(new rosterEntry(entry.getUser(), false));
        }


        mAdapter = new xmppRosterAdapter(roster_sender);
        mRecyclerView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        }
//        Button bLoadFromServer = (Button) findViewById(R.id.bLoadFromServer);
//        assert bLoadFromServer != null;
//        bLoadFromServer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, DownloadMediaActivity.class);
//                DownloadMediaActivity.session = session;
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (connection != null)
            connection.disconnect();
        super.onBackPressed();
    }

    private class rosterEntry {
        String rosterName;
        boolean newMessage;

        public rosterEntry(String rosterName, boolean newMessage) {
            this.rosterName = rosterName;
            this.newMessage = newMessage;
        }
    }

    private class xmppRosterAdapter extends RecyclerView.Adapter<xmppRosterAdapter.ViewHolder> {

        public ArrayList<rosterEntry> rosterEntries;

        public xmppRosterAdapter(ArrayList<rosterEntry> rosterEntries) {
            this.rosterEntries = rosterEntries;
        }

        @Override
        public xmppRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppRosterAdapter.ViewHolder holder, int position) {
            if (rosterEntries.get(position).newMessage)
                holder.itemView.setBackgroundColor(Color.parseColor("#ff9900"));
            holder.tvRosterName.setText(rosterEntries.get(position).rosterName);
            holder.itemView.setTag("duas@sinecos.local");
        }

        @Override
        public int getItemCount() {
            return rosterEntries.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvRosterName;

            public ViewHolder(View view) {
                super(view);
                this.tvRosterName = (TextView) view.findViewById(R.id.tvContactName);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Chat newChat = chatManager.createChat("duas@sinecos.local");
                XmppChatActivity.chat = newChat;
                Intent intent = new Intent(XmppActivity.this, XmppChatActivity.class);
                startActivity(intent);
            }
        }
    }

}
