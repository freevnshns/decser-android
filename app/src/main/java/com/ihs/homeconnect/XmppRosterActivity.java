package com.ihs.homeconnect;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihs.homeconnect.Entities.ChatMessageModel;
import com.ihs.homeconnect.Entities.RosterModel;
import com.ihs.homeconnect.helpers.OrmHelper;
import com.ihs.homeconnect.helpers.VerticalSpaceDecorationHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XmppRosterActivity extends AppCompatActivity implements ChatManagerListener {

    XmppService xmppService;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    AbstractXMPPConnection connection;
    ChatManager chatManager;
    OrmHelper ormHelper;
    Dao<ChatMessageModel, Integer> chatMessagesDao;

    private ServiceConnection xmppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            XmppService.PacketBinder packetBinder = (XmppService.PacketBinder) iBinder;
            xmppService = packetBinder.getService();
            connection = xmppService.getAbstractXMPPConnection();
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(XmppRosterActivity.this);
            mAdapter = new xmppRosterAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, XmppService.class);
        bindService(intent, xmppServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(xmppServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp);
        ormHelper = OpenHelperManager.getHelper(this, OrmHelper.class);
        try {
            chatMessagesDao = ormHelper.getDao(ChatMessageModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rvXmppContacts);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(this));
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally)
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    try {
                        chatMessagesDao.create(new ChatMessageModel(chat.getParticipant().substring(0, chat.getParticipant().lastIndexOf("/")), message.getBody(), false, false));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private class xmppRosterAdapter extends RecyclerView.Adapter<xmppRosterAdapter.ViewHolder> {

        List<RosterModel> rosterModelEntries = new ArrayList<>();

        xmppRosterAdapter() {
            rosterModelEntries = xmppService.getRosterModels();
        }

        @Override
        public xmppRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppRosterAdapter.ViewHolder holder, int position) {
            if (rosterModelEntries.get(position).isNew_flag()) {
                holder.itemView.setBackgroundColor(Color.parseColor("#ff9900"));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
//            Strip domain name if req.
            holder.tvRosterName.setText(rosterModelEntries.get(position).getId());
            holder.itemView.setTag(rosterModelEntries.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return rosterModelEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvRosterName;

            ViewHolder(View view) {
                super(view);
                this.tvRosterName = (TextView) view.findViewById(R.id.tvContactName);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                XmppChatActivity.chat = chatManager.createChat(v.getTag().toString());
                v.setBackgroundColor(Color.TRANSPARENT);
                Intent intent = new Intent(XmppRosterActivity.this, XmppChatActivity.class);
                startActivity(intent);
            }
        }
    }
}
