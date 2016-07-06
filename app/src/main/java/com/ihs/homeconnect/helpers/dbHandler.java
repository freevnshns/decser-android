package com.ihs.homeconnect.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class dbHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 14;
    //    TABLE 1 DD STARTS HERE
    public static final String DATABASE_NAME = "homeConnect.db";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NAME_TYPE = "TEXT";

    public static final String COLUMN_HOST_NAME = "hostname";
    public static final String COLUMN_HOST_NAME_TYPE = "TEXT";

    //    TABLE 2 DD STARTS HERE
    public static final String TABLE_AVAILABLE_SERVICES = "available_services";

    public static final String COLUMN_SERVICE_ID = "serviceId";
    public static final String COLUMN_SERVICE_ID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_SERVICE_NAME = "serviceName";
    public static final String COLUMN_SERVICE_NAME_TYPE = "TEXT UNIQUE";

    public static final String COLUMN_SERVICE_STATUS = "serviceStatus";
    public static final String COLUMN_SERVICE_STATUS_TYPE = "INTEGER";

    public static final String COLUMN_SERVICE_PORT = "servicePort";
    public static final String COLUMN_SERVICE_PORT_TYPE = "INTEGER";


    public dbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "\n(\n" + COLUMN_ID + " " + COLUMN_ID_TYPE + " , " + COLUMN_NAME + " " + COLUMN_NAME_TYPE + " , " + COLUMN_HOST_NAME + " " + COLUMN_HOST_NAME_TYPE + "\n);";
        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_AVAILABLE_SERVICES + "\n(\n" + COLUMN_SERVICE_ID + " " + COLUMN_SERVICE_ID_TYPE + " , " + COLUMN_SERVICE_NAME + " " + COLUMN_SERVICE_NAME_TYPE + " , " + COLUMN_SERVICE_STATUS + " " + COLUMN_SERVICE_STATUS_TYPE + " , " + COLUMN_SERVICE_PORT + " " + COLUMN_SERVICE_PORT_TYPE + "\n);";
        try {
            db.execSQL(query);
            db.execSQL(query2);
            populateServices(db);
        } catch (SQLException e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVAILABLE_SERVICES);
        onCreate(db);
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

    public void addContact(String name, String hostname) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_HOST_NAME, hostname);
        try {
            db.insert(TABLE_CONTACTS, null, values);
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        db.close();
    }

    public void populateServices(SQLiteDatabase db) {
        for (services ser : services.values()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SERVICE_NAME, ser.toString());
            values.put(COLUMN_SERVICE_STATUS, 1);
            values.put(COLUMN_SERVICE_PORT, ser.port);
            try {
                db.insert(TABLE_AVAILABLE_SERVICES, null, values);
            } catch (Exception e) {
                loggingHandler loggingHandler = new loggingHandler();
                loggingHandler.addLog(e.getMessage());
            }
        }
    }

    public ArrayList<String> getServices(int status) {
        ArrayList<String> servicesList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_AVAILABLE_SERVICES + " WHERE " + COLUMN_SERVICE_STATUS + "==" + status + ";";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_SERVICE_NAME)) != null) {
                    servicesList.add(index, c.getString(c.getColumnIndex(COLUMN_SERVICE_NAME)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
            return servicesList;
        } catch (Exception e) {
            loggingHandler loggingHandler = new loggingHandler();
            loggingHandler.addLog(e.getMessage());
        }
        return null;
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
}
