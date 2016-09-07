package com.ihs.homeconnect.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class dbHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 17;
    public static final String DATABASE_NAME = "homeConnect.db";

    //    Table UserData
    public static final String TABLE_USERDATA = "userdata";

    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_EMAIL_TYPE = "TEXT PRIMARY KEY";

    public static final String COLUMN_USER_HOST_NAME = "hostname";
    public static final String COLUMN_USER_HOST_NAME_TYPE = "TEXT";

    public static final String COLUMN_REGISTERED = "registered";
    public static final String COLUMN_REGISTERED_TYPE = "INTEGER";

    public static final String COLUMN_BACKUP_SET = "bkp_set";
    public static final String COLUMN_BACKUP_SET_TYPE = "INTEGER";


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

    public static final String TABLE_XMPP_MESSAGES = "xmpp_messages";

    public static final String COLUMN_MID = "mid";
    public static final String COLUMN_MID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_MESSAGE_BODY = "body";
    public static final String COLUMN_MESSAGE_BODY_TYPE = "TEXT";

    public static final String COLUMN_SENDER_ID = "sender";
    public static final String COLUMN_SENDER_ID_TYPE = "TEXT";

    //    Table BackupEntity
    public static final String TABLE_BACKUP = "backup";

    public static final String COLUMN_PID = "pid";
    public static final String COLUMN_PID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_BACKUP_PATH = "bkp_path";
    public static final String COLUMN_BACKUP_PATH_TYPE = "TEXT";

    public static final String COLUMN_AUTO_BACKUP = "auto_bkp";
    public static final String COLUMN_AUTO_BACKUP_TYPE = "INTEGER";


    public dbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE IF NOT EXISTS " + TABLE_USERDATA + "\n(\n" + COLUMN_USER_EMAIL + " " + COLUMN_USER_EMAIL_TYPE + " , " + COLUMN_USER_HOST_NAME + " " + COLUMN_USER_HOST_NAME_TYPE + " , " + COLUMN_REGISTERED + " " + COLUMN_REGISTERED_TYPE + " , " + COLUMN_BACKUP_SET + " " + COLUMN_BACKUP_SET_TYPE + "\n);";
        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "\n(\n" + COLUMN_ID + " " + COLUMN_ID_TYPE + " , " + COLUMN_NAME + " " + COLUMN_NAME_TYPE + " , " + COLUMN_HOST_NAME + " " + COLUMN_HOST_NAME_TYPE + "," + COLUMN_ACCESS + " " + COLUMN_ACCESS_TYPE + "\n);";
        String query3 = "CREATE TABLE IF NOT EXISTS " + TABLE_XMPP_MESSAGES + "\n(\n" + COLUMN_MID + " " + COLUMN_MID_TYPE + " , " + COLUMN_MESSAGE_BODY + " " + COLUMN_MESSAGE_BODY_TYPE + " , " + COLUMN_SENDER_ID + " " + COLUMN_SENDER_ID_TYPE + "\n);";
        String query4 = "CREATE TABLE IF NOT EXISTS " + TABLE_BACKUP + "\n(\n" + COLUMN_PID + " " + COLUMN_PID_TYPE + " , " + COLUMN_BACKUP_PATH + " " + COLUMN_BACKUP_PATH_TYPE + " , " + COLUMN_AUTO_BACKUP + " " + COLUMN_AUTO_BACKUP_TYPE + "\n);";

        try {
            db.execSQL(query1);
            db.execSQL(query2);
            db.execSQL(query3);
            db.execSQL(query4);
        } catch (SQLException e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_XMPP_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BACKUP);
        onCreate(db);
    }

    public boolean insertUserDetails(String email, String hostname, int registered) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_HOST_NAME, hostname);
        values.put(COLUMN_BACKUP_SET, 0);
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

    public void setColumnBackupSet() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("UPDATE " + TABLE_BACKUP + " SET " + COLUMN_BACKUP_SET + " = 1 WHERE 1;");
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
    }

    public int getUserRegistration() {
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

    public String getUserHostname() {
        SQLiteDatabase db = getReadableDatabase();
        String user_hostname = null;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERDATA + " WHERE 1;", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_USER_HOST_NAME)) != null) {
                    user_hostname = c.getString(c.getColumnIndex(COLUMN_USER_HOST_NAME));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user_hostname;
    }

    public int isBackupSet() {
        SQLiteDatabase db = getReadableDatabase();
        int backed = 0;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERDATA + " WHERE 1;", null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_BACKUP_SET)) != null) {
                    backed = c.getInt(c.getColumnIndex(COLUMN_BACKUP_SET));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return backed;
    }

    public String getUserEmail() {
        SQLiteDatabase db = getReadableDatabase();
        String user_email = null;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERDATA + " WHERE 1;", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_USER_EMAIL)) != null) {
                    user_email = c.getString(c.getColumnIndex(COLUMN_USER_EMAIL));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user_email;
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

    public void addMessage(String sender, String body) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, sender);
        values.put(COLUMN_MESSAGE_BODY, body);
        try {
            db.insert(TABLE_XMPP_MESSAGES, null, values);
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
    }

    public ArrayList<String> getMessages(String participant) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        String query = "SELECT " + COLUMN_MESSAGE_BODY + " FROM " + TABLE_XMPP_MESSAGES + " WHERE " + COLUMN_SENDER_ID + "='" + participant + "';";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_MESSAGE_BODY)) != null) {
                    arrayList.add(c.getString(c.getColumnIndex(COLUMN_MESSAGE_BODY)));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void insertBackupPaths(String path, int auto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BACKUP_PATH, path);
        values.put(COLUMN_AUTO_BACKUP, auto);
        try {
            db.insert(TABLE_BACKUP, null, values);
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
    }

    public ArrayList<String> getBackupPaths(int auto) {
        ArrayList<String> paths = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_BACKUP_PATH + " FROM " + TABLE_BACKUP + " WHERE " + COLUMN_AUTO_BACKUP + "='" + auto + "';";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_BACKUP_PATH)) != null) {
                    paths.add(c.getString(c.getColumnIndex(COLUMN_BACKUP_PATH)));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
