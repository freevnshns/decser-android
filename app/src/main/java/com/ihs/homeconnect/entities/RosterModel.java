package com.ihs.homeconnect.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "xmpp_roster")
public class RosterModel {
    @DatabaseField
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private boolean new_flag;

    public RosterModel() {
    }

    public RosterModel(String id, String name, boolean new_flag) {
        this.id = id;
        this.name = name;
        this.new_flag = new_flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNew_flag() {
        return new_flag;
    }

    public void setNew_flag(boolean new_flag) {
        this.new_flag = new_flag;
    }
}
