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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  Hola specific
*/
import com.hola.serverSide.ariApplication.db.beans.ActiveHandover;

public class ActiveHandoversHelper
{
    private static final Logger log = LoggerFactory.getLogger (ActiveHandoversHelper.class.getName ());

    public static int putInActiveHandoversTable (ActiveHandover activeHandover)
    {
	int putActiveHandoverReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		QueryRunner getAnActiveHandoverQuery = new QueryRunner ();
		/*
		  mysql> describe activeHandovers;
		  +----------------+------------------+------+-----+---------+-------+
		  | Field          | Type             | Null | Key | Default | Extra |
		  +----------------+------------------+------+-----+---------+-------+
		  | uuid           | char(35)         | NO   | MUL | NULL    |       |
		  | extension      | int(10) unsigned | NO   | MUL | NULL    |       |
		  | callerID       | varchar(20)      | NO   |     | NULL    |       |
		  | callerHandle   | varchar(20)      | NO   | MUL | NULL    |       |
		  | callerChannel  | varchar(20)      | NO   |     | NULL    |       |
		  | callerOrCallee | varchar(10)      | NO   |     | NULL    |       |
		  +----------------+------------------+------+-----+---------+-------+
		  6 rows in set (0.02 sec)
		  
		  mysql> INSERT INTO activeHandovers (uuid, extension, callerID, callerHandle, callerChannel, callerOrCallee) 
		  VALUES ("sillyUUID", 1000, "callerID", "callerHandle", "calleeChannel", "callee");
		  Query OK, 1 row affected (0.03 sec)
		  
		  mysql> select * from activeHandovers;
		  +-----------+-----------+----------+--------------+---------------+----------------+
		  | uuid      | extension | callerID | callerHandle | callerChannel | callerOrCallee |
		  +-----------+-----------+----------+--------------+---------------+----------------+
		  | sillyUUID |      1000 | callerID | callerHandle | calleeChannel | callee         |
		  +-----------+-----------+----------+--------------+---------------+----------------+
		  1 row in set (0.02 sec)
		  
		 */
		putActiveHandoverReturnCode = 
		    getAnActiveHandoverQuery.update (connectionFromHikariCP, 
						     "INSERT INTO activeHandovers " + 
						     " (uuid, extension, callerID, callerHandle, callerChannel, callerOrCallee) " +
						     "VALUES (?, ?, ?, ?, ?, ?)",
						     new Object [] 
						     {activeHandover.getUUID (), activeHandover.getExtension (),
						      activeHandover.getCallerID (), activeHandover.getCallerHandle (), 
						      activeHandover.getCallerChannel (), activeHandover.getCallerOrCallee ()});
		//log.debug ("The result of this transaction is " + putActiveHandoverReturnCode);
	    }
	catch (SQLException e)
	    {
		System.out.println ("Error putting activeHandover into activeHandovers table in pacifi DB: " +
				    activeHandover.getUUID () + " " + e.toString ());
		log.error ("Error putting activeHandover {} into activeHandovers table in pacifi DB", activeHandover.getUUID ());
                DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		//log.debug ("Closing connection....");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putActiveHandoverReturnCode;
	    }

    }

    public static ActiveHandover getActiveHandoverByUUID (String UUID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveHandover activeHandover = null;
	try
	    {
		QueryRunner getAnActiveHandoverQuery = new QueryRunner ();
		activeHandover = 
		    (ActiveHandover) getAnActiveHandoverQuery.query (connectionFromHikariCP, 
								     "SELECT * FROM activeHandovers WHERE uuid=?",
								     new BeanHandler <ActiveHandover> (ActiveHandover.class), 
								     UUID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching active handover {} from activeHandovers table in pacifi DB", 
			   UUID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeHandover;
	    }
    }

    public static ActiveHandover getActiveHandoverByUUIDAndChannelID (String UUID, String channelID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveHandover activeHandover = null;
	try
	    {
		QueryRunner getAnActiveHandoverQuery = new QueryRunner ();
		activeHandover = 
		    (ActiveHandover) getAnActiveHandoverQuery.query (connectionFromHikariCP, 
								     "SELECT * FROM activeHandovers WHERE uuid=? AND " +
								     "callerChannel=?",
								     new BeanHandler <ActiveHandover> (ActiveHandover.class), 
								     UUID, channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching active handover {}/{} from activeHandovers table in pacifi DB", 
			   UUID, channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeHandover;
	    }
    }

    public static ActiveHandover getActiveHandoverByExtension (int extension)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveHandover activeHandover = null;
	try
	    {
		QueryRunner getAnActiveHandoverQuery = new QueryRunner ();
		activeHandover = 
		    (ActiveHandover) 
		    getAnActiveHandoverQuery.query (connectionFromHikariCP, 
						    "SELECT * FROM activeHandovers WHERE extension=?",
						    new BeanHandler <ActiveHandover> (ActiveHandover.class), 
						    Integer.toString (extension));
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching call record for {} from activeHandovers table in pacifi DB", 
			   extension);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeHandover;
	    }
    }

    public static List <ActiveHandover> getAllFromActiveHandoversTable ()
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <ActiveHandover> activeHandovers = null;
	try
	    {
		QueryRunner getAActiveHandoverQuery = new QueryRunner ();
		activeHandovers = 
		    (List <ActiveHandover>) 
		    getAActiveHandoverQuery.query (connectionFromHikariCP, "SELECT * FROM activeHandovers",
						   new BeanListHandler <ActiveHandover> (ActiveHandover.class));
		log.debug ("Size of fetched activeHandovers = {}", activeHandovers.size ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all activeHandovers for from activeHandovers table in pacifi DB");
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeHandovers;
	    }
    }

    public static ActiveHandover getByChannelID (String channelID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveHandover activeHandover = null;
	try
	    {
		QueryRunner getAActiveHandoverQuery = new QueryRunner ();
		activeHandover = (ActiveHandover) 
		    getAActiveHandoverQuery.query (connectionFromHikariCP, 
					       "SELECT * FROM activeHandovers WHERE callerChannel=?",
					       new BeanHandler <ActiveHandover> (ActiveHandover.class), channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for {} from activeHandovers table in pacifi DB", channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeHandover;
	    }
    }

    public static int updateActiveHandoverWithExtensionAndHandoverRequestor (String callerChannel, int extension,
									     String handoverRequestor)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner activeHandoverQuery = new QueryRunner ();
		updateResult = activeHandoverQuery.update (connectionFromHikariCP,
							   "UPDATE activeHandovers SET extension=?, callerOrCallee=? " +
							   " WHERE callerChannel=?",
							   extension, handoverRequestor, callerChannel);
	    }
	catch (SQLException e)
	    {
		log.error ("Error updating an active handover for caller info {}/{} for extension ", 
			   callerChannel, extension, handoverRequestor);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static int updateActiveHandoverWithExtension (String callerChannel, int extension)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner activeHandoverQuery = new QueryRunner ();
		updateResult = activeHandoverQuery.update (connectionFromHikariCP,
							   "UPDATE activeHandovers SET extension=? WHERE callerChannel=?",
							   extension, callerChannel);
	    }
	catch (SQLException e)
	    {
		log.error ("Error updating an active handover for caller info {}/{} for extension ", 
			   callerChannel, extension);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }


    public static int updateActiveHandoverWithCallerOrCallee (String channelID, String type)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner activeHandoverQuery = new QueryRunner ();
		updateResult = activeHandoverQuery.update (connectionFromHikariCP,
							   "UPDATE activeHandovers SET callerOrCallee=? WHERE callerChannel=?",
							   type, channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error updating an active handover for channel {} with caller or callee flag", channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static int removeActiveHandoverEntry (String callerChannel)
    {
        Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
        int deleteResult = 0;
        try
            {
                QueryRunner getAActiveHandoverQuery = new QueryRunner ();

                deleteResult = getAActiveHandoverQuery.update (connectionFromHikariCP,
							       "DELETE FROM activeHandovers WHERE callerChannel=?",
							       callerChannel);
            }
        catch (SQLException e)
            {
                log.error ("Error running SQl query for {} from activeHandovers table in pacifi DB {}", 
			   callerChannel, e.toString ());
                DbUtils.closeQuietly (connectionFromHikariCP);
                return -1;
            }
        finally
            {
                DbUtils.closeQuietly (connectionFromHikariCP);
                return deleteResult;
            }
    }
 }
