/*
 * The MIT License
 *
 * Copyright 2018 Derick Felix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jloquent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author derickfelix
 * @date Feb 24, 2018
 */
public class Database {
    
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jloquent";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";
    private static Connection connection;
    
    public static Connection open() {
        try {
            
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Failed to open connection", e);
        }
        
        return connection;
    }
    
    public static void execute(String sql) {
        Database.open();
        try (Statement statement = connection.createStatement()) {    
            statement.execute(sql);
        } catch (SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Failed to execute statement", e);
        }
        Database.close();
    }
    public static ResultSet executeQuery(String sql) {
        Database.open();
        try {    
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            return resultSet;
        } catch (SQLException e) {            
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Failed to execute query", e);
        }
        Database.close();
        
        return null;
    }
    
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Failed to close connection", e);
        }
    }   
    
    public static Object getResult(ResultSet rs, String type, String column) throws SQLException {
        switch (type) {
            case "int":
            case "Integer":
                return rs.getInt(column);
            case "double":
            case "Double":
                return rs.getDouble(column);
            case "boolean":
            case "Boolean":
                return rs.getBoolean(column);
            case "char":
            case "Character":
                return rs.getString(column).charAt(0);
            case "Array":
                return rs.getArray(column);
            case "String":
                return rs.getString(column);
        }
        return null;
    }
}