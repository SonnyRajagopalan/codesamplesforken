package com.hola.serverSide.appInterface.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.io.IOException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/*
  Hola specific
*/
import com.hola.serverSide.appInterface.db.beans.AccountHandles;
import com.hola.serverSide.appInterface.db.beans.HolaAccount;
import com.hola.serverSide.appInterface.islands.HandleType;
import com.hola.serverSide.appInterface.appi.Contact;
import com.hola.serverSide.appInterface.appi.HandleStatus;

public class AccountHandlesHelper
{
    private static final Logger log = LoggerFactory.getLogger (AccountHandlesHelper.class.getName ());

    private static final ObjectMapper objectMapper = new ObjectMapper ();

    public static int putAccountHandles (AccountHandles accountHandles)
    {
	int putAccountHandleReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		/*

		  mysql> describe accountHandles;
		  +-----------------+---------------+------+-----+---------+-------+
		  | Field           | Type          | Null | Key | Default | Extra |
		  +-----------------+---------------+------+-----+---------+-------+
		  | usernameOrEmail | varchar(100)  | NO   | PRI | NULL    |       |
		  | extension       | int(10)       | NO   | MUL | NULL    |       |
		  | handles         | varchar(2000) | NO   |     | NULL    |       |
		  +-----------------+---------------+------+-----+---------+-------+

		  mysql> insert into  accountHandles (usernameOrEmail, extension, handles)
		  VALUES  ("ddas@pacifi.net",  6002,  "SILLY_HANDLES")  ON  DUPLICATE  KEY
		  UPDATE handles=VALUES (handles);

		  Query OK, 1 row affected (0.02 sec)
		  
		  mysql> select * from accountHandles;
		  +-----------------+-----------+---------------+
		  | usernameOrEmail | extension | handles       |
		  +-----------------+-----------+---------------+
		  | ddas@pacifi.net |      6002 | SILLY_HANDLES |
		  +-----------------+-----------+---------------+
		  1 row in set (0.02 sec)
		  
		  mysql> insert into  accountHandles (usernameOrEmail, extension, handles)
		  VALUES  ("ddas@pacifi.net", 6002,  "SILLIER_HANDLES")  ON DUPLICATE  KEY
		  UPDATE handles=VALUES (handles);

		  Query OK, 2 rows affected (0.02 sec)
		  
		  mysql> select * from accountHandles;
		  +-----------------+-----------+-----------------+
		  | usernameOrEmail | extension | handles         |
		  +-----------------+-----------+-----------------+
		  | ddas@pacifi.net |      6002 | SILLIER_HANDLES |
		  +-----------------+-----------+-----------------+
		  1 row in set (0.02 sec)


		*/
		QueryRunner putAccountHandlesForUserQuery = new QueryRunner ();
		putAccountHandleReturnCode = 
		    putAccountHandlesForUserQuery.update (connectionFromHikariCP,					  
							  "INSERT INTO accountHandles (usernameOrEmail, extension, handles) " +
							  " VALUES (?, ?, ?)  ON DUPLICATE KEY" +
							  " UPDATE handles=VALUES (handles)",
							  new Object [] 
							  {accountHandles.getUsernameOrEmail (), 
							   accountHandles.getExtension (), 
							   accountHandles.getHandles ()});
		
		log.debug ("The result of this transaction is {}", putAccountHandleReturnCode);
		return putAccountHandleReturnCode;
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting user {}'s account handles into accountHandles table in pacifi DB", 
			   accountHandles.getUsernameOrEmail ());
		e.printStackTrace ();
		DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		//log.debug ("Closing connection....");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putAccountHandleReturnCode;
	    }

    }
    public static int updateAccountHandlesForUsernameOrEmail (String usernameOrEmail, String handles)
    {
        Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
        int updateResult = -1;
        try
            {
                QueryRunner updateHandlesQuery = new QueryRunner ();
                updateResult = updateHandlesQuery.update (connectionFromHikariCP,
							  "UPDATE accountHandles SET handles=? WHERE usernameOrEmail=?",
							  handles, usernameOrEmail);
            }
        catch (SQLException e)
            {
		log.error ("Error updating {}'s account handles with {} in accountHandles table in pacifi DB", 
			   usernameOrEmail, handles);
		DbUtils.closeQuietly (connectionFromHikariCP);
                return updateResult;
            }
        finally
            {
		DbUtils.closeQuietly (connectionFromHikariCP);
                return updateResult;
	    }
    }

    public static AccountHandles getAccountHandlesByUsernameOrEmail (String usernameOrEmail)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	AccountHandles accountHandles = null;
	try
	    {
		QueryRunner getAccountHandlesQuery = new QueryRunner ();
		accountHandles = 
		    (AccountHandles) getAccountHandlesQuery.query (connectionFromHikariCP, 
								   "SELECT * FROM accountHandles WHERE usernameOrEmail=?",
								  new BeanHandler <AccountHandles> (AccountHandles.class), 
								     usernameOrEmail);
		log.debug ("Retrieved {}", accountHandles.toJsonString ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching account handles for {} from accountHandles table in pacifi DB", 
			   usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return accountHandles;
	    }
    }

    public static List <AccountHandles> getAllAccountHandles ()
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <AccountHandles> allAccountHandles = null;
	try
	    {
		QueryRunner getAccountHandlesQuery = new QueryRunner ();
		allAccountHandles = (List <AccountHandles>) 
		    getAccountHandlesQuery.query (connectionFromHikariCP, 
						  "SELECT * FROM accountHandles",
						  new BeanListHandler <AccountHandles> (AccountHandles.class));
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all records from from accountHandles table in pacifi DB");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return allAccountHandles;
	    }
    }

    public static String getAppHandleForUsernameOrEmail (String usernameOrEmail, String app, String method, boolean ignoreStatus)
    {
	AccountHandles accountHandles = getAccountHandlesByUsernameOrEmail (usernameOrEmail);

	/*
	  [{"app":"voip","method":"voice","handle":"","status":true},
	   {"app":"mobile","method":"text","handle":"+13392061413","status":false},
	   {"app":"mobile","method":"voice","handle":"+13392061413","status":false}]
	 */
	try
	    {
		List <HandleStatus>
		    handleStatusesJsonList = objectMapper.readValue (accountHandles.getHandles (),
							       new TypeReference<List <HandleStatus>> () {});
		
		for (HandleStatus handleStatus: handleStatusesJsonList)
		    {
			if (handleStatus.getApp ().equals (app) && 
			    handleStatus.getMethod ().equals (method))
			    {
				if (ignoreStatus == true)
				    {
					return handleStatus.getHandle ();
				    }
				else // Status-sensitive retrieval of handle
				    {
					if (handleStatus.getStatus ())
					    {
						return handleStatus.getHandle ();
					    }
					else
					    {
						return null;
					    }
				    }
			    }
		    }
	    }
	catch (IOException e)
	    {
		log.error ("Could not cast HandleStatus JSON list string into List <HandleStatus>", accountHandles.getHandles ());
	    }
	return null;
    }

    public static HolaAccount getHolaAccountFromHandle (String handle)
    {
	List <AccountHandles> allAccountHandles = getAllAccountHandles ();

	for (AccountHandles accountHandles: allAccountHandles)
	    {
		if (accountHandles.hasHandle (handle))
		    {
			return HolaAccountsHelper.getHolaAccountByUsernameOrEmail (accountHandles.getUsernameOrEmail ());
		    }
	    }
	return null;
    }
}
