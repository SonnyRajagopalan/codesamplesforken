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
import com.hola.serverSide.appInterface.db.beans.CurrentStatus;
import com.hola.serverSide.appInterface.islands.HandleType;
import com.hola.serverSide.appInterface.appi.Contact;

public class CurrentStatusHelper
{
    private static final Logger log = LoggerFactory.getLogger (CurrentStatusHelper.class.getName ());

    public static int putCurrentStatusInCurrentStatusDB (CurrentStatus currentStatus)
    {
	int putCurrentStatusReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		/*
		  mysql> describe currentStatus;
		  +-----------------+------------------+------+-----+---------+-------+
		  | Field           | Type             | Null | Key | Default | Extra |
		  +-----------------+------------------+------+-----+---------+-------+
		  | usernameOrEmail | char(100)        | NO   | PRI | NULL    |       |
		  | extension       | int(10) unsigned | NO   | MUL | NULL    |       |
		  | doNotDisturb    | tinyint(1)       | NO   |     | 0       |       |
		  | voice           | tinyint(1)       | NO   |     | 1       |       |
		  | text            | tinyint(1)       | NO   |     | 1       |       |
		  | video           | tinyint(1)       | NO   |     | 1       |       |
		  | updateCount     | int(10)          | NO   |     | 0       |       |
		  +-----------------+------------------+------+-----+---------+-------+

		  mysql> insert into currentStatus (usernameOrEmail, doNotDisturb, voice, text, video, updateCount) 
		  VALUES ("ddas@pacifi.net", true, true, true, false, 1) ON DUPLICATE KEY UPDATE
		  updateCount = updateCount+1, doNotDisturb=VALUES (doNotDisturb), voice=VALUES (voice), 
		  text=VALUES (text), video=VALUES (video);
		  Query OK, 2 rows affected (0.03 sec)

		*/
		QueryRunner getAUserQuery = new QueryRunner ();
		putCurrentStatusReturnCode = getAUserQuery.update (connectionFromHikariCP, 

								   "INSERT INTO currentStatus (usernameOrEmail, extension, " +
								   "doNotDisturb, voice, text, video, updateCount) " +
								   "VALUES (?, ?, ?, ?, ?, ?, ?)  ON DUPLICATE KEY" +
								   " UPDATE updateCount = updateCount + 1, " +
								   "doNotDisturb=VALUES (doNotDisturb), " +
								   "voice=VALUES (voice), text=VALUES (text), " +
								   "video=VALUES (video)",
								   new Object [] 
								   {currentStatus.getUsernameOrEmail (), 
								    currentStatus.getExtension (),
								    currentStatus.getDoNotDisturb (), 
								    currentStatus.getVoice (), 
								    currentStatus.getText (), 
								    currentStatus.getVideo (), 1});

		log.debug ("The result of this transaction is {}", putCurrentStatusReturnCode);
		return putCurrentStatusReturnCode;
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting user {}'s currentStatus into currentStatus DB", currentStatus.getUsernameOrEmail ());
		e.printStackTrace ();
		DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		//log.debug ("Closing connection....");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putCurrentStatusReturnCode;
	    }

    }

    public static CurrentStatus getCurrentStatusFromCurrentStatusDBByUsernameOrEmail (String usernameOrEmail)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	CurrentStatus currentStatus = null;
	try
	    {
		QueryRunner getAUserQuery = new QueryRunner ();
		currentStatus = (CurrentStatus) getAUserQuery.query (connectionFromHikariCP, 
								     "SELECT * FROM currentStatus WHERE usernameOrEmail=?",
								     new BeanHandler <CurrentStatus> (CurrentStatus.class), 
								     usernameOrEmail);
		log.debug ("Retrieved {}", currentStatus.toJsonString ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching user record for {} from currentStatus DB", usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return currentStatus;
	    }
    }

    public static boolean getCurrentStatusFromCurrentStatusDBByType (String usernameOrEmail, String infoType)
    {
	CurrentStatus currentStatus = getCurrentStatusFromCurrentStatusDBByUsernameOrEmail (usernameOrEmail);

	switch (infoType)
	    {
	    case "doNotDisturb":
		{
		    return currentStatus.getDoNotDisturb ();
		}
	    case "voice":
		{
		    return currentStatus.getVoice ();
		}
	    case "text":
		{
		    return currentStatus.getText ();
		}
	    case "video":
		{
		    return currentStatus.getVideo ();
		}
	    default:
		return false;
	    }
    }
}
