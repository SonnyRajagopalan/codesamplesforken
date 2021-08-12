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
import com.hola.serverSide.ariApplication.db.beans.AccountFriend;
import com.hola.serverSide.ariApplication.islands.HandleType;
import com.hola.serverSide.ariApplication.crypto.PasswordHash;
import com.hola.serverSide.ariApplication.db.beans.Contact;
//import com.hola.serverSide.ariApplication.common.ThreadUtils;

public class AccountFriendsHelper
{
    private static final Logger log = LoggerFactory.getLogger (AccountFriendsHelper.class.getName ());

    public static int putAccountFriend (String usernameOrEmail, String friend)
    {
	/*
	  mysql> INSERT IGNORE INTO friends (usernameOrEmail, friend) VALUES ("ddas", "sillyFriend");
	  Query OK, 1 row affected (0.03 sec)
	  
	  mysql> INSERT IGNORE INTO friends (usernameOrEmail, friend) VALUES ("ddas", "sillyFriend");
	  Query OK, 0 rows affected (0.02 sec)
	  
	  mysql> select * from friends;
	  +-----------------+-------------+
	  | usernameOrEmail | friend      |
	  +-----------------+-------------+
	  | ddas            | sillyFriend |
	  +-----------------+-------------+
	  1 row in set (0.02 sec)


	*/
	int putFriendReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		QueryRunner putFriendQuery = new QueryRunner ();
		putFriendReturnCode = 
		    putFriendQuery.update (connectionFromHikariCP, 
					   "INSERT IGNORE INTO accountFriends (usernameOrEmail, friend) VALUES (?, ?)",
					   usernameOrEmail, friend);
		log.debug ("The result of this transaction to insert ({}, {}) is {}", usernameOrEmail, friend, 
			   putFriendReturnCode);
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting relationship [{} IS_A_FRIEND_OF {}] into accountFriends table in pacifi DB", 
			   friend, usernameOrEmail);
		e.printStackTrace ();
		DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putFriendReturnCode;
	    }

    }

    public static List <AccountFriend> getAllFriendships ()
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <AccountFriend> allFriendships = null;
	try
	    {
		QueryRunner getAllFriendsQuery = new QueryRunner ();
		allFriendships = (List <AccountFriend>) 
		    getAllFriendsQuery.query (connectionFromHikariCP, "SELECT * FROM accountFriends",
					      new BeanListHandler <AccountFriend> (AccountFriend.class));
		log.debug ("Size of fetched accounts = {}", allFriendships.size ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all friendships from accountFriends table in pacifi DB");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return allFriendships;
	    }
    }

    public static List <AccountFriend> getFriendsForUsernameOrEmail (String usernameOrEmail)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <AccountFriend> allAccountFriendships = null;
	try
	    {
		QueryRunner getAllAccountFriendsQuery = new QueryRunner ();
		allAccountFriendships = (List <AccountFriend>) 
		    getAllAccountFriendsQuery.query (connectionFromHikariCP, "SELECT * FROM accountFriends WHERE usernameOrEmail=?",
					      new BeanListHandler <AccountFriend> (AccountFriend.class), usernameOrEmail);
		log.debug ("Size of fetched accounts = {}", allAccountFriendships.size ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all friendships for {} from accountFriends table in pacifi DB", 
			   usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return allAccountFriendships;
	    }
    }
}

