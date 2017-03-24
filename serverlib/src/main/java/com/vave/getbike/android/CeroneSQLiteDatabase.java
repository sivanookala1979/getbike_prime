/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import android.util.Log;

import com.vave.getbike.datasource.BaseDataSource;
import com.vave.getbike.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public class CeroneSQLiteDatabase implements ISQLiteDatabase {

    SqliteManager manager = new SqliteManager();

    public CeroneSQLiteDatabase() {
        manager.open();
    }

    @Override
    public long insert(String tableName, String object, IContentValues newValues) {
        Connection connection = manager.getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "insert into " + tableName + "(" + getCommaSeparatedColumns(tableName) + ") values (" + getCommaSeparatedValues(newValues, tableName) + ")";
            System.out.println(query);
            AndroidStubsFactory.getLogger().d("TAG","insert query in CeroneSQLiteDatabase is:"+query);
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(String tableName, String whereClause, String[] whereArgs) {
        Connection connection = manager.getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "delete from " + tableName;
            if (whereClause != null) {
                query += " where " + whereClause;
            }
            System.out.println(query);
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ICursor query(String tableName, String[] allColumns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Connection connection = manager.getConnection();
        CeroneCursor result = new CeroneCursor();
        try {
            Statement statement = connection.createStatement();
            String query = "select * from " + tableName;
            if (selection != null && !selection.trim().isEmpty()) {
                query += " where " + selection;
            }
            System.out.println(query);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (String column : allColumns) {
                    row.put(column, resultSet.getString(column));
                }
                if (resultSet.getString("id") != null) {
                    row.put("id", resultSet.getLong("id"));
                }
                result.addRow(row);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int update(String tableName, IContentValues newValues, String whereClause, String[] whereArgs) {
        Connection connection = manager.getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "update " + tableName + " set ";
            BaseDataSource relevantDataSource = DatabaseHelper.getRelevantDataSource(tableName, null);
            String[] columnNames = relevantDataSource.getAllColumns();
            for (String columnName : columnNames) {
                Object value = ((CeroneContentValues) newValues).get(columnName);
                if (value != null) {
                    query += columnName + " = '" + value + "',";
                }
            }
            query = query.trim().endsWith(",") ? query.substring(0, query.length() - 1) : query;
            if (whereClause != null) {
                query += " where " + whereClause;
            }
            System.out.println(query);
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getCommaSeparatedColumns(String tableName) {
        BaseDataSource relevantDataSource = DatabaseHelper.getRelevantDataSource(tableName, null);
        String[] columnNames = relevantDataSource.getAllColumns();
        String result = "";
        for (int index = 0; index < columnNames.length; index++) {
            String columnName = columnNames[index];
            result += columnName + (index != columnNames.length - 1 ? ", " : "");
        }
        return result;
    }

    private String getCommaSeparatedValues(IContentValues newValues, String tableName) {
        BaseDataSource relevantDataSource = DatabaseHelper.getRelevantDataSource(tableName, null);
        String[] columnNames = relevantDataSource.getAllColumns();
        String result = "";
        for (int index = 0; index < columnNames.length; index++) {
            String columnName = columnNames[index];
            result += "'" + ((CeroneContentValues) newValues).get(columnName) + "'" + (index != columnNames.length - 1 ? ", " : "");
        }
        return result;
    }

    @Override
    public long create(String tableName, String[] allColumns) {
        Connection connection = manager.getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = DatabaseHelper.createTable(tableName, allColumns);
            System.out.println(query);
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
