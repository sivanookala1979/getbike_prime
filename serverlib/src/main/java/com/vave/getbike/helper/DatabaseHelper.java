/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vave.getbike.android.IDatabaseHelper;
import com.vave.getbike.datasource.BaseDataSource;
import com.vave.getbike.datasource.RideLocationDataSource;

/**
 * @author cerone
 * @version 1.0, Apr 14, 2015
 */
public class DatabaseHelper extends SQLiteOpenHelper implements IDatabaseHelper {

    public static final String[] ALL_TABLES = {
            RideLocationDataSource.RIDE_LOCATION_TABLE
    };
    public static final BaseDataSource[] ALL_DATASOURCES = {

            new RideLocationDataSource(null)
    };
    private static final int VERSION = 5;
    private static final String RIDE_LOCATION_DATA_SOURCE = createTable(RideLocationDataSource.RIDE_LOCATION_TABLE, RideLocationDataSource.ALL_COLUMNS);

    public DatabaseHelper(Context context) {
        super(context, "getbike.db", null, VERSION);
    }

    public static String createTable(String tableName, String... columnNames) {
        String result = "create table if not exists " + tableName + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,";
        for (int index = 0; index < columnNames.length; index++) {
            String columnName = columnNames[index];
            result += columnName + (index != columnNames.length - 1 ? " TEXT, " : " TEXT");
        }
        result += ");";
        return result;
    }

    public static String createCustomTable(String tableName, String query) {
        String result = "create table if not exists " + tableName + "(";
        result += query;
        result += ");";
        return result;
    }

    public static BaseDataSource getRelevantDataSource(String tableName, Context context) {
        for (int i = 0; i < ALL_TABLES.length; i++) {
            if (ALL_TABLES[i].equals(tableName)) {
                return ALL_DATASOURCES[i];
            }
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RIDE_LOCATION_DATA_SOURCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + RideLocationDataSource.RIDE_LOCATION_TABLE);
        onCreate(database);
    }

    @Override
    public SQLiteDatabase getWritableDatabase2() {
        return super.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public void clearDB() {
        SQLiteDatabase database = getWritableDatabase();
        for (String tableName : ALL_TABLES) {
            database.execSQL("DELETE FROM " + tableName);
        }
        database.close();
    }
}
