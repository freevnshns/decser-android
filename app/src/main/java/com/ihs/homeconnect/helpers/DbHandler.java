package com.ihs.homeconnect.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 22;
    private static final String DATABASE_NAME = "homeConnect.db";

    //    Table UserData
    private static final String TABLE_USERDATA = "userdata";

    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_EMAIL_TYPE = "TEXT PRIMARY KEY";

    private static final String COLUMN_USER_HOST_NAME = "hostname";
    private static final String COLUMN_USER_HOST_NAME_TYPE = "TEXT";

    private static final String COLUMN_REGISTERED = "registered";
    private static final String COLUMN_REGISTERED_TYPE = "INTEGER";

    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSWORD_TYPE = "TEXT";


    //    TABLE contacts
    private static final String TABLE_CONTACTS = "contacts";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NAME_TYPE = "TEXT";

    private static final String COLUMN_HOST_NAME = "hostname";
    private static final String COLUMN_HOST_NAME_TYPE = "TEXT";

    private static final String COLUMN_ACCESS = "access";
    private static final String COLUMN_ACCESS_TYPE = "TEXT";

//    Table xmpp messages

    private static final String TABLE_XMPP_MESSAGES = "xmpp_messages";

    private static final String COLUMN_MID = "mid";
    private static final String COLUMN_MID_TYPE = "INTEGER PRIMARY KEY";

    private static final String COLUMN_MESSAGE_BODY = "body";
    private static final String COLUMN_MESSAGE_BODY_TYPE = "TEXT";

    private static final String COLUMN_SENDER_ID = "sender";
    private static final String COLUMN_SENDER_ID_TYPE = "TEXT";

    //    Table BackupEntity
    private static final String TABLE_BACKUP = "backup";

    private static final String COLUMN_BACKUP_PATH = "bkp_path";
    private static final String COLUMN_BACKUP_PATH_TYPE = "TEXT PRIMARY KEY";

    private static final String COLUMN_AUTO_BACKUP = "auto_bkp";
    private static final String COLUMN_AUTO_BACKUP_TYPE = "INTEGER";


    public DbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE IF NOT EXISTS " + TABLE_USERDATA + "\n(\n" + COLUMN_USER_EMAIL + " " + COLUMN_USER_EMAIL_TYPE + " , " + COLUMN_USER_HOST_NAME + " " + COLUMN_USER_HOST_NAME_TYPE + " , " + COLUMN_REGISTERED + " " + COLUMN_REGISTERED_TYPE + " , " + COLUMN_PASSWORD + " " + COLUMN_PASSWORD_TYPE + "\n);";
        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "\n(\n" + COLUMN_ID + " " + COLUMN_ID_TYPE + " , " + COLUMN_NAME + " " + COLUMN_NAME_TYPE + " , " + COLUMN_HOST_NAME + " " + COLUMN_HOST_NAME_TYPE + "," + COLUMN_ACCESS + " " + COLUMN_ACCESS_TYPE + "\n);";
        String query3 = "CREATE TABLE IF NOT EXISTS " + TABLE_XMPP_MESSAGES + "\n(\n" + COLUMN_MID + " " + COLUMN_MID_TYPE + " , " + COLUMN_MESSAGE_BODY + " " + COLUMN_MESSAGE_BODY_TYPE + " , " + COLUMN_SENDER_ID + " " + COLUMN_SENDER_ID_TYPE + "\n);";
        String query4 = "CREATE TABLE IF NOT EXISTS " + TABLE_BACKUP + "\n(\n" + COLUMN_BACKUP_PATH + " " + COLUMN_BACKUP_PATH_TYPE + " , " + COLUMN_AUTO_BACKUP + " " + COLUMN_AUTO_BACKUP_TYPE + "\n);";

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

    public boolean insertUserDetails(String email, String hostname, int registered, String pass) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email + "@decser.com");
        values.put(COLUMN_USER_HOST_NAME, hostname);
        values.put(COLUMN_PASSWORD, pass);
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

//    public void updateColumnPassword() {
//        SQLiteDatabase db = getWritableDatabase();
//        try {
//            db.execSQL("UPDATE " + TABLE_USERDATA + " SET " + COLUMN_PASSWORD + " = 1 WHERE 1;");
//        } catch (Exception e) {
//            loggingHandler loggingHandler = new loggingHandler();
//            loggingHandler.addLog(e.getMessage());
//        }
//        db.close();
//    }

    public String getUserPassword() {
        SQLiteDatabase db = getReadableDatabase();
        String pass = null;
        try {
            Cursor c = db.rawQuery("SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERDATA + " WHERE 1;", null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_PASSWORD)) != null) {
                    pass = c.getString(c.getColumnIndex(COLUMN_PASSWORD));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pass;
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

    public void insertBackupPaths(String path, int auto) throws SQLiteConstraintException {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BACKUP_PATH, path);
        values.put(COLUMN_AUTO_BACKUP, auto);
        try {
            db.insert(TABLE_BACKUP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void deleteBackupPath(String path) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_BACKUP, COLUMN_BACKUP_PATH + " == '" + path + "'", null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
