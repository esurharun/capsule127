package com.capsule127;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by marcus on 12/01/14.
 */
public class LocalStorageManager {

    public static Connection storageConnection = null;

    public static void open() {

        try {


            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            storageConnection = DriverManager.getConnection("jdbc:hsqldb:file:c127db", "SA", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {


        try {
            if (storageConnection != null)
                storageConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        storageConnection = null;

    }


}
