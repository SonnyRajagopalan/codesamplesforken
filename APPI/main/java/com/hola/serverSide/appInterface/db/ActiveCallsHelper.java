/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.appInterface.db;

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
import com.hola.serverSide.appInterface.db.beans.ActiveCall;

public class ActiveCallsHelper
{
    private static final Logger log = LoggerFactory.getLogger (ActiveCallsHelper.class.getName ());

    public static int putInActiveCallsTable (ActiveCall activeCall)
    {
	int putActiveCallReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		QueryRunner getAnActiveCallQuery = new QueryRunner ();
		/*
		  mysql> insert into activeCalls (uuid, callerChannel, callerID, calleeChannel, calleeID, zombie) 
		  VALUES ("sillyUUID", "callerC", "callerID", "calleeC", "calleeID", false);
		  Query OK, 1 row affected (0.03 sec)

		 */
		putActiveCallReturnCode = 
		    getAnActiveCallQuery.update (connectionFromHikariCP, 
						 "INSERT INTO activeCalls " + 
						 " (uuid, callerChannel, callerID, callerHandle, " +
						 "        calleeChannel, calleeID, calleeHandle, zombie) " +
						 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
						 new Object [] 
						 {activeCall.getUUID (), activeCall.getCallerChannel (),
						  activeCall.getCallerID (), activeCall.getCallerHandle (),
						  activeCall.getCalleeChannel (), activeCall.getCalleeID (), 
						  activeCall.getCalleeHandle (), activeCall.getZombie ()});
		//log.debug ("The result of this transaction is " + putActiveCallReturnCode);
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting activeCall {} into activeCalls table in pacifi DB", activeCall.getUUID ());
                DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		//log.debug ("Closing connection....");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putActiveCallReturnCode;
	    }

    }

    public static ActiveCall getByActiveCallUUID (String UUID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	try
	    {
		QueryRunner getAnActiveCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) getAnActiveCallQuery.query (connectionFromHikariCP, 
								      "SELECT * FROM activeCalls WHERE uuid=?",
								      new BeanHandler <ActiveCall> (ActiveCall.class), UUID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching call record for {} from activeCalls table in pacifi DB", UUID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCall;
	    }
    }

    public static ActiveCall getActiveCallByExtension (int extension)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	try
	    {
		QueryRunner getAnActiveCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) getAnActiveCallQuery.query (connectionFromHikariCP, 
								      "SELECT * FROM activeCalls WHERE calleeID=? OR callerID=?",
								      new BeanHandler <ActiveCall> (ActiveCall.class), 
								      Integer.toString (extension), Integer.toString (extension));
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching call record for {} from activeCalls table in pacifi DB", 
			   extension);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCall;
	    }
    }

    public static List <ActiveCall> getAllFromActiveCallsTable ()
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <ActiveCall> activeCalls = null;
	try
	    {
		QueryRunner getAActiveCallQuery = new QueryRunner ();
		activeCalls = 
		    (List <ActiveCall>) getAActiveCallQuery.query (connectionFromHikariCP, "SELECT * FROM activeCalls",
								   new BeanListHandler <ActiveCall> (ActiveCall.class));
		log.debug ("Size of fetched activeCalls = {}", activeCalls.size ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all activeCalls for from activeCalls table in pacifi DB");
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCalls;
	    }
    }

    public static ActiveCall getByChannelID (String channelID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	try
	    {
		QueryRunner getAActiveCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) 
		    getAActiveCallQuery.query (connectionFromHikariCP, 
					       "SELECT * FROM activeCalls WHERE callerChannel=? OR calleeChannel=?",
					       new BeanHandler <ActiveCall> (ActiveCall.class), channelID, channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for {} from activeCalls table in pacifi DB", channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCall;
	    }
    }

    public static ActiveCall getByCallerChannelID (String channelID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	try
	    {
		QueryRunner getAActiveCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) 
		    getAActiveCallQuery.query (connectionFromHikariCP, 
					       "SELECT * FROM activeCalls WHERE callerChannel=?",
					       new BeanHandler <ActiveCall> (ActiveCall.class), channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for {} from activeCalls table in pacifi DB", channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCall;
	    }
    }

    public static ActiveCall getByCalleeChannelID (String channelID)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	try
	    {
		QueryRunner getAActiveCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) 
		    getAActiveCallQuery.query (connectionFromHikariCP, 
					       "SELECT * FROM activeCalls WHERE calleeChannel=?",
					       new BeanHandler <ActiveCall> (ActiveCall.class), channelID);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for {} from activeCalls table in pacifi DB", channelID);
                DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return activeCall;
	    }
    }
    
    public static int updateActiveCallWithNewCalleeInfo (String UUID, String channelID, String calleeHandle)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner activeCallQuery = new QueryRunner ();
		updateResult = activeCallQuery.update (connectionFromHikariCP,
						       "UPDATE activeCalls SET calleeChannel=?, calleeHandle=? WHERE uuid=?",
						       channelID, calleeHandle, UUID);
	    }
	catch (SQLException e)
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		log.error ("Error updating activeCalls with for active call {} new callee info {}/{}", 
			   UUID, channelID, calleeHandle);
		return updateResult;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static int updateActiveCallWithNewCallerInfo (String UUID, String channelID, String callerHandle)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner activeCallQuery = new QueryRunner ();
		updateResult = activeCallQuery.update (connectionFromHikariCP,
						       "UPDATE activeCalls SET callerChannel=?, callerHandle=? WHERE uuid=?",
						       channelID, callerHandle, UUID);
	    }
	catch (SQLException e)
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		log.error ("Error updating activeCalls with for active call {} new caller info {}/{}", 
			   UUID, channelID, callerHandle);
		return updateResult;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static String getUnfinishedCallUUIDFromCalleeID (String calleeID)
    {
        Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
        ActiveCall activeCall = null;
        try
            {
                QueryRunner getAActiveCallQuery = new QueryRunner ();
                activeCall = (ActiveCall)
                    getAActiveCallQuery.query (connectionFromHikariCP,
                                               "SELECT * FROM activeCalls WHERE calleeChannel=? AND calleeID=?",
                                               new BeanHandler <ActiveCall> (ActiveCall.class),
                                               "CALLEE_CHANNEL", calleeID);
            }
        catch (SQLException e)
            {
                log.error ("Error running SQl query for {} from activeCalls table in pacifi DB", calleeID);
                DbUtils.closeQuietly (connectionFromHikariCP);
                return null;
            }
        finally
            {
                DbUtils.closeQuietly (connectionFromHikariCP);
                return activeCall.getUUID ();
            }
    }

    public static int removeUnfinishedCallFromCalleeID (String calleeID)
    {
        Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
        int deleteResult = 0;
        try
            {
                QueryRunner getAActiveCallQuery = new QueryRunner ();

                deleteResult = getAActiveCallQuery.update (connectionFromHikariCP,
                                                           "DELETE FROM activeCalls WHERE calleeChannel=? AND calleeID=?",
                                                           "CALLEE_CHANNEL", calleeID);
            }
        catch (SQLException e)
            {
                log.error ("Error running SQl query for {} from activeCalls table in pacifi DB", calleeID);
                DbUtils.closeQuietly (connectionFromHikariCP);
                return -1;
            }
        finally
            {
                DbUtils.closeQuietly (connectionFromHikariCP);
                return deleteResult;
            }
    }

    public static boolean findAndSetCallZombieStatus (String channelID)
    {
	/*
	  if call is not marked zombie
             mark it zombie;
	     return false
	  else
	     delete active call;
	     return true

	  E.g.:
	  mysql> update activeCalls set zombie=true where uuid="sillyUUID";
	  Query OK, 1 row affected (0.08 sec)

	  mysql> update activeCalls set zombie=false where callerChannel="callerC";
	  Query OK, 1 row affected (0.02 sec)

	  mysql> update activeCalls set zombie=true where calleeChannel="callerC" or callerChannel="callerC";
	  Query OK, 1 row affected (0.03 sec)
	 */
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	ActiveCall activeCall = null;
	boolean status = false;
	try
	    {
		QueryRunner activeCallQuery = new QueryRunner ();
		activeCall = (ActiveCall) 
		    activeCallQuery.query (connectionFromHikariCP, 
					   "SELECT * FROM activeCalls WHERE callerChannel=? OR calleeChannel=?",
					   new BeanHandler <ActiveCall> (ActiveCall.class), channelID, channelID);
		if (activeCall.getZombie ())
		    {
			int deleteResult = 
			    activeCallQuery.update (connectionFromHikariCP,
						    "DELETE FROM activeCalls WHERE calleeChannel=? OR callerChannel=?",
						    channelID, channelID);
			status = true;
		    }
		else
		    {
			int updateResult = 
			    activeCallQuery.update (connectionFromHikariCP,
						    "UPDATE activeCalls SET zombie=true WHERE calleeChannel=? OR callerChannel=?",
						    channelID, channelID);
			status = false;
		    }
	    }
	catch (SQLException e)
	    {
		log.error ("Error figuring out zombie status for call with channel ID {}", channelID);
		//e.printStackTrace ();
                DbUtils.closeQuietly (connectionFromHikariCP);
		return false;
	    }
	finally
	    {
                DbUtils.closeQuietly (connectionFromHikariCP);
		return status;
	    }
    }

    public static boolean callWithChannelIsMarkedZombie (String channelID)
    {
	ActiveCall activeCall = getByChannelID (channelID);
	
	if (activeCall.getZombie ())
	    {
		return true;
	    }
	else
	    {
		return false;
	    }
    }
 }
