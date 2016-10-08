package com.decser.connect.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "xmpp_messages")
public class ChatMessageModel {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String sender;
    @DatabaseField
    private String body;
    @DatabaseField
    private boolean new_flag;
    @DatabaseField
    private boolean self_flag;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String sender, String body, boolean new_flag, boolean self_flag) {
        this.sender = sender;
        this.body = body;
        this.new_flag = new_flag;
        this.self_flag = self_flag;
    }

    public boolean isSelf_flag() {
        return self_flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isNew_flag() {
        return new_flag;
    }

    public void setNew_flag(boolean new_flag) {
        this.new_flag = new_flag;
    }
}
