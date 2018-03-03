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

package org.jloquent.core;

/**
 *
 * @author derickfelix
 * @date Mar 3, 2018
 */
public class Database {

    static String jdbc_driver = "com.mysql.jdbc.Driver";
    static String type = "jdbc:mysql://";
    
    /**
     * Database's host, <code>localhost</code> is set by default.
     */
    public static String host = "localhost";
    
    /**
     * Database's port, <code>3306</code> is set  by default.
     */
    public static String port = "3306";
    
    /**
     * Database's name, <code>homestead</code> is set  by default.
     */
    public static String name = "homestead";
    
    /**
     * Database's username, <code>homestead</code> is set  by default.
     */
    public static String username = "homestead";
    
    /**
     * Database's password, empty is set by default.
     */
    public static String password = "";

    /**
     * A constant that represents a <code>MYSQL</code> database.
     */
    public static final int MYSQL_DB = 0;
    /**
     * A constant that represents a <code>POSTGRESQL</code> database.
     */
    public static final int POSTGRES_DB = 1;

    /**
     * Sets the database type that will be used
     * 
     * @param dbType an integer representing the type of a database.
     */
    public static void setDatabaseType(int dbType) {
        switch (dbType) {
            case MYSQL_DB:
                jdbc_driver = "com.mysql.jdbc.Driver";
                type = "jdbc:mysql://";
                break;
            case POSTGRES_DB:
                jdbc_driver = "org.postgres.jdbc.Driver";
                type = "jdbc:postgres://";
                break;
            default:
                System.out.println("unknown database type");
        }
    }

}
