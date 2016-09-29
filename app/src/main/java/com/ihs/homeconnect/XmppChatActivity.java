package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.OrmHelper;
import com.ihs.homeconnect.models.ChatMessage;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;
import java.util.List;

public class XmppChatActivity extends AppCompatActivity {
    public static Chat chat;
    OrmHelper ormHelper = OpenHelperManager.getHelper(this, OrmHelper.class);
    private String chatParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp_chat);
        chatParticipant = chat.getParticipant();
        final Button bSendChatMessage = (Button) findViewById(R.id.bSendMessage);
        final EditText etChatMessage = (EditText) findViewById(R.id.etChatMessage);
        final RecyclerView mRecyclerView;
        final RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvChatConsole);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        final xmppChatAdapter chatAdapter = new xmppChatAdapter(chatParticipant);
        mAdapter = chatAdapter;
        mRecyclerView.setAdapter(mAdapter);


        chat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.messageUpdate(message.getBody(), false);
                        mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
                    }
                });
            }
        });

        if (bSendChatMessage != null && etChatMessage != null)
            bSendChatMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String message = etChatMessage.getText().toString();
                                chat.sendMessage(message);
                                chatAdapter.messageUpdate(message, true);
                                mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
                                etChatMessage.setText("");
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
    }

    private class xmppChatAdapter extends RecyclerView.Adapter<xmppChatAdapter.ViewHolder> {
        List<ChatMessage> chatEntries;
        Dao<ChatMessage, Integer> chatMessages;

        xmppChatAdapter(String participant) {
            try {
                chatMessages = ormHelper.getDao(ChatMessage.class);
                chatEntries = chatMessages.queryForEq("sender", participant);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        void messageUpdate(String messageString, boolean chatMe) {
            if (!messageString.equals("")) {
                ChatMessage chatMessage = new ChatMessage(chatParticipant, messageString, false, chatMe);
                chatEntries.add(chatMessage);
                try {
                    chatMessages.create(chatMessage);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public xmppChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppChatAdapter.ViewHolder holder, int position) {
            if (chatEntries.get(holder.getAdapterPosition()).isSelf_flag()) {
                holder.tvChatMessageLocal.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
            } else {
                holder.tvChatMessageRemote.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
            }
        }

        @Override
        public int getItemCount() {
            return chatEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvChatMessageLocal;
            TextView tvChatMessageRemote;

            ViewHolder(View view) {
                super(view);
                this.tvChatMessageLocal = (TextView) view.findViewById(R.id.tvChatMessageLocal);
                this.tvChatMessageRemote = (TextView) view.findViewById(R.id.tvChatMessageRemote);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
