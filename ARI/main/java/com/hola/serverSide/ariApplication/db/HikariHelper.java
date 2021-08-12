/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */

package com.hola.serverSide.ariApplication.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.lang.Thread;
import java.lang.StackTraceElement;

import com.zaxxer.hikari.HikariDataSource;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariHelper
{
    private static final Logger log = LoggerFactory.getLogger (HikariHelper.class.getName());

    public static Connection getADBConnection ()
    {
	HikariDataSource ds   = HikariConnectionPool.getInstance ().getDataSource ();
	Connection connection = null;

	try
	    {
		connection = ds.getConnection ();
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		if (connection == null)
		    {
			log.error ("Connection object is NULL");
		    }		    
	    }

	return connection;
    }

    public static void turnOffAutocommit (Connection connection)
    {
	try
	    {
		connection.setAutoCommit (false);
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		//...
	    }
    }

    public static void commitToDB (Connection connection)
    {
	try
	    {
		connection.commit ();
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		// ...
	    }
    }

    public static void rollbackDB (Connection connection)
    {
	try
	    {
		connection.rollback ();
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		// ...
	    }
    }

    public static void turnOnAutocommit (Connection connection)
    {
	try
	    {
		connection.setAutoCommit (true);
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		//...
	    }
    }

    public static PreparedStatement initializePreparedStatement (Connection connection, String SQLString)
    {
	PreparedStatement ps = null;

	try
	    {
		connection.prepareStatement (SQLString);
	    }
	catch (SQLException e)
	    {
		handleSQLException (e);
	    }
	finally
	    {
		if (ps == null)
		    {
			log.error ("Could not initialize PreparedStatement for SQLString [" + SQLString + "]");
		    }
	    }
	return ps;
    }

    public static void handleSQLException (SQLException e)
    {
	StackTraceElement [] stackTrace = Thread.currentThread().getStackTrace ();
	StackTraceElement callerElement = stackTrace [3];
	String caller = stackTrace [4].getMethodName () + "." + stackTrace [3].getMethodName ();

	log.debug (caller + ": State [" + e.getSQLState () +  "] Error code [" + e.getErrorCode () + "] Message [" + e.getMessage ());
	e.printStackTrace ();
    }

    public static void closeDBConnection (Connection c)
    {
	if (c != null)
	    {
		try
		    {
			c.close ();
		    }
		catch (SQLException e)
		    {
			handleSQLException (e);
		    }
	    }
    }

    public static void closeDBResultSet (ResultSet rs)
    {
	if (rs != null)
	    {
		try
		    {
			rs.close ();
		    }
		catch (SQLException e)
		    {
			handleSQLException (e);
		    }
	    }
    }

    public static void closeDBPreparedStatement (PreparedStatement p)
    {
	if (p != null)
	    {
		try
		    {
			p.close ();
		    }
		catch (SQLException e)
		    {
			handleSQLException (e);
		    }
	    }
    }
}
