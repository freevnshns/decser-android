package com.ihs.homeconnect;

import android.graphics.Color;
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

import com.ihs.homeconnect.helpers.dbHandler;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class XmppChatActivity extends AppCompatActivity {
    public static Chat chat;
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
        public ArrayList<chatMessage> chatEntries;
        private dbHandler dbHandler = new dbHandler(XmppChatActivity.this, null);

        public xmppChatAdapter(String participant) {
            chatEntries = new ArrayList<>();
            ArrayList<String> offline_chats = dbHandler.getMessages(participant);
            while (!offline_chats.isEmpty()) {
                chatEntries.add(new chatMessage(offline_chats.remove(0), false));
            }
        }

        public void messageUpdate(String messageString, boolean chatMe) {
            if (!messageString.equals("")) {
                chatEntries.add(new chatMessage(messageString, chatMe));
                if (chatMe)
                    dbHandler.addMessage(chatParticipant, "Me : > " + messageString);
                else
                    dbHandler.addMessage(chatParticipant, " : > " + messageString);
                notifyDataSetChanged();
            }
        }

        @Override
        public xmppChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_chat_bubble, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppChatAdapter.ViewHolder holder, int position) {
            if (chatEntries.get(holder.getAdapterPosition()).messageIsMine) {
                holder.tvChatBody.setTextColor(Color.GRAY);
            } else {
                holder.tvChatBody.setTextColor(Color.BLACK);
            }
            holder.tvChatBody.setText(chatEntries.get(holder.getAdapterPosition()).chatBody);
        }

        @Override
        public int getItemCount() {
            return chatEntries.size();
        }

        private class chatMessage {
            public String chatBody;
            public boolean messageIsMine;

            public chatMessage(String chatBody, boolean messageIsMine) {
                this.chatBody = chatBody;
                this.messageIsMine = messageIsMine;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvChatBody;

            public ViewHolder(View view) {
                super(view);
                this.tvChatBody = (TextView) view.findViewById(R.id.tvChatMessage);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
