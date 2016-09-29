package com.ihs.homeconnect.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ihs.homeconnect.models.ChatMessage;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class OrmHelper extends OrmLiteSqliteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "homeConnectv2.db";

    public OrmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ChatMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ChatMessage.class, true);
            TableUtils.createTable(connectionSource, ChatMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
