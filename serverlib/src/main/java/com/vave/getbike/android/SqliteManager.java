/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;



import com.vave.getbike.exception.GetBikeException;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqliteManager {

    Connection connection = null;

    public Connection getConnection() {
        return connection;
    }

    public Connection open() {
    	return open("adzshop.db");
    }

	public Connection open(String dbName) {
		try {
            Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" +
            		dbName);
            return connection;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            throw new GetBikeException(e);
        }
	}

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
        }
    }
}