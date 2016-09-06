package com.ihs.homeconnect.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class dbHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 16;
    public static final String DATABASE_NAME = "homeConnect.db";

    //    Table UserData
    public static final String TABLE_USERDATA = "userdata";

    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_EMAIL_TYPE = "TEXT PRIMARY KEY";

    public static final String COLUMN_USER_HOST_NAME = "hostname";
    public static final String COLUMN_USER_HOST_NAME_TYPE = "TEXT";

    public static final String COLUMN_REGISTERED = "registered";
    public static final String COLUMN_REGISTERED_TYPE = "INTEGER";


    //    TABLE contacts
    public static final String TABLE_CONTACTS = "contacts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NAME_TYPE = "TEXT";

    public static final String COLUMN_HOST_NAME = "hostname";
    public static final String COLUMN_HOST_NAME_TYPE = "TEXT";

    public static final String COLUMN_ACCESS = "access";
    public static final String COLUMN_ACCESS_TYPE = "TEXT";

//    Table xmpp messages

//    public static final String TABLE_XMPP_MESSAGES="xmpp_messages";
//
//    public static final String COLUMN_ = "id";
//    public static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";

    public dbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE IF NOT EXISTS " + TABLE_USERDATA + "\n(\n" + COLUMN_EMAIL + " " + COLUMN_EMAIL_TYPE + " , " + COLUMN_USER_HOST_NAME + " " + COLUMN_USER_HOST_NAME_TYPE + " , " + COLUMN_REGISTERED + " " + COLUMN_REGISTERED_TYPE + "\n);";
        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "\n(\n" + COLUMN_ID + " " + COLUMN_ID_TYPE + " , " + COLUMN_NAME + " " + COLUMN_NAME_TYPE + " , " + COLUMN_HOST_NAME + " " + COLUMN_HOST_NAME_TYPE + "," + COLUMN_ACCESS + " " + COLUMN_ACCESS_TYPE + "\n);";
        try {
            db.execSQL(query1);
            db.execSQL(query2);
        } catch (SQLException e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public boolean insertUserDetails(String email, String hostname, int registered) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_USER_HOST_NAME, hostname);
        values.put(COLUMN_REGISTERED, registered);
        try {
            db.insert(TABLE_USERDATA, null, values);
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
            return false;
        }
        db.close();
        return true;
    }

    public int getUserDetails() {
        SQLiteDatabase db = getReadableDatabase();
        int registered = 0;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERDATA + " WHERE 1;", null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_REGISTERED)) != null) {
                    registered = c.getInt(c.getColumnIndex(COLUMN_REGISTERED));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registered;

    }

    public boolean deleteContact(String hostname) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_CONTACTS, COLUMN_HOST_NAME + " == '" + hostname + "'", null);
            db.close();
            return true;
        } catch (SQLException e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
        return false;
    }

    public void addContact(String name, String hostname, String access) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_HOST_NAME, hostname);
        values.put(COLUMN_ACCESS, access);
        try {
            db.insert(TABLE_CONTACTS, null, values);
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
    }

    public String[] getContactNameList() {
        ArrayList<String> tempContactsList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE 1;";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null) {
                    tempContactsList.add(index, c.getString(c.getColumnIndex(COLUMN_NAME)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] contactsArray = new String[tempContactsList.size()];
        for (int i = 0; i < tempContactsList.size(); i++) {
            contactsArray[i] = tempContactsList.get(i);
        }
        return contactsArray;
    }

    public String[] getHostnameArray() {
        ArrayList<String> tempHostnameList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE 1;";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null) {
                    tempHostnameList.add(index, c.getString(c.getColumnIndex(COLUMN_HOST_NAME)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] hostnameArray = new String[tempHostnameList.size()];
        for (int i = 0; i < tempHostnameList.size(); i++) {
            hostnameArray[i] = tempHostnameList.get(i);
        }
        return hostnameArray;
    }

    public String getAccessType(String hostname) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_ACCESS + " FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_HOST_NAME + "='" + hostname + "';";
        String ac_lvl = "limited-user";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            ac_lvl = c.getString(c.getColumnIndex(COLUMN_ACCESS));
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ac_lvl;
    }
}
