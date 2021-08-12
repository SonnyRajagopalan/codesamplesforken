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
import com.hola.serverSide.appInterface.db.beans.HolaAccount;
import com.hola.serverSide.appInterface.db.beans.AccountFriend;
import com.hola.serverSide.appInterface.islands.HandleType;
import com.hola.serverSide.appInterface.crypto.PasswordHash;
import com.hola.serverSide.appInterface.appi.Contact;
//import com.hola.serverSide.appInterface.common.ThreadUtils;

public class HolaAccountsHelper
{
    private static final Logger log = LoggerFactory.getLogger (HolaAccountsHelper.class.getName ());

    public static int putHolaAccount (HolaAccount account)
    {
	/*
	  mysql> insert into holaAccounts (usernameOrEmail, firstname, lastname) VALUES ("silly@email.com", "Silly", "Man");
	  Query OK, 1 row affected (0.00 sec)
	*/
	int putHolaAccountReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		QueryRunner getAHolaAccountQuery = new QueryRunner ();
		putHolaAccountReturnCode = 
		    getAHolaAccountQuery.update (connectionFromHikariCP, 
						 "INSERT INTO holaAccounts " + 
						 "(usernameOrEmail, firstname, lastname) " +
						 "VALUES (?, ?, ?)",
						 new Object [] 
						 {account.getUsernameOrEmail (), account.getFirstname (), 
						  account.getLastname ()});
		log.info ("Added an Hola account for {}, return code was {}", account.getUsernameOrEmail (),
			  putHolaAccountReturnCode);
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting account {} into accounts table in pacifi DB", account.getUsernameOrEmail ());
		e.printStackTrace ();
		DbUtils.closeQuietly (connectionFromHikariCP);
		return -1;
	    }
	finally
	    {
		log.debug ("Inserted Hola account for {} into accounts DB.", account.getUsernameOrEmail ());
		DbUtils.closeQuietly (connectionFromHikariCP);
		return putHolaAccountReturnCode;
	    }

    }

    public static HolaAccount getHolaAccountByUsernameOrEmail (String usernameOrEmail)
    {
	//log.debug (ThreadUtils.getThreadIDedStackTraceString ());
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	HolaAccount account = null;
	try
	    {
		QueryRunner getAHolaAccountQuery = new QueryRunner ();
		account = (HolaAccount) 
		    getAHolaAccountQuery.query (connectionFromHikariCP, "SELECT * FROM holaAccounts WHERE usernameOrEmail=?",
						new BeanHandler <HolaAccount> (HolaAccount.class), usernameOrEmail);
	    }
	catch (SQLException e)
	    {
		log.error ("Error in SQl query for fetching account {} from holaAccounts table in pacifi DB", usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		log.debug ("Fetched record for account ID {}", usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return account;
	    }
    }

    public static int getExtensionForUsernameOrEmail (String usernameOrEmail)
    {
	HolaAccount account = getHolaAccountByUsernameOrEmail (usernameOrEmail);
	return account.getExtension ();
    }

    public static List <HolaAccount> getAllHolaAccounts ()
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <HolaAccount> accounts = null;
	try
	    {
		QueryRunner getAHolaAccountQuery = new QueryRunner ();
		accounts = (List <HolaAccount>) 
		    getAHolaAccountQuery.query (connectionFromHikariCP, "SELECT * FROM holaAccounts",
						new BeanListHandler <HolaAccount> (HolaAccount.class));
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all accounts from holaAccounts table in pacifi DB");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		log.debug ("Size of fetched accounts = {}", accounts.size ());
		DbUtils.closeQuietly (connectionFromHikariCP);
		return accounts;
	    }
    }

    public static List <Contact> getContactsForUser (String usernameOrEmail)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	List <Contact> contacts = new ArrayList <Contact> ();
	List <AccountFriend> friends = null;

	try
	    {
		QueryRunner contactQuery = new QueryRunner ();
		friends = (List <AccountFriend>) 
		    contactQuery.query (connectionFromHikariCP, 
					"SELECT * FROM accountFriends WHERE usernameOrEmail=?",
					new BeanListHandler <AccountFriend> (AccountFriend.class),
					usernameOrEmail);
		log.debug ("Size of fetched accounts = {}", friends.size ());

		QueryRunner accountsQuery = new QueryRunner ();
		for (AccountFriend friend: friends)
		    {
			log.debug ("Contact username = {}", friend.getFriend ());

			HolaAccount holaAccount = accountsQuery.query (connectionFromHikariCP,
								       "SELECT * FROM holaAccounts WHERE usernameOrEmail=?",
								       new BeanHandler <HolaAccount> (HolaAccount.class),
								       friend.getFriend ());
			Contact contact = new Contact (holaAccount.getFirstname (), holaAccount.getLastname (),
						       holaAccount.getUsernameOrEmail (), 
						       Integer.toString (holaAccount.getExtension ()));
			contacts.add (contact);
		    }
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching all accounts from holaAccounts table in pacifi DB");
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return contacts;
	    }
    }

    public static HolaAccount getHolaAccountByExtension (int extension)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	HolaAccount account = null;
	try
	    {
		QueryRunner getAHolaAccountQuery = new QueryRunner ();
		account = (HolaAccount) 
		    getAHolaAccountQuery.query (connectionFromHikariCP, "SELECT * FROM holaAccounts WHERE extension=?",
						new BeanHandler <HolaAccount> (HolaAccount.class), extension);
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching extension {} from holaAccounts in pacifi DB", extension);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return account;
	    }
    }
    
    public static int updateHolaAccountPolicies (int extension, String policies)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner accountPoliciesQuery = new QueryRunner ();
		updateResult = accountPoliciesQuery.update (connectionFromHikariCP,
							    "UPDATE accountPolicies SET policies=? WHERE extension=?",
							    policies, extension);
	    }
	catch (SQLException e)
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static int updateHolaAccountHandles (int extension, String handles)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner accountHandlesQuery = new QueryRunner ();
		updateResult = accountHandlesQuery.update (connectionFromHikariCP,
							    "UPDATE accountHandles SET handles=? WHERE extension=?",
							    handles, extension);
	    }
	catch (SQLException e)
	    {
		return updateResult;
	    }
	finally
	    {
		return updateResult;
	    }
    }

    public static int updateHolaAccountPolicies (String usernameOrEmail, String policies)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner accountPoliciesQuery = new QueryRunner ();
		updateResult = accountPoliciesQuery.update (connectionFromHikariCP,
						     "UPDATE accountPolicies SET policies=? WHERE usernameOrEmail=?",
							    policies, usernameOrEmail);
	    }
	catch (SQLException e)
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }

    public static int updateHolaAccountHandles (String usernameOrEmail, String handles)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	int updateResult = -1;
	try
	    {
		QueryRunner accountHandlesQuery = new QueryRunner ();
		updateResult = accountHandlesQuery.update (connectionFromHikariCP,
						     "UPDATE accountHandles SET handles=? WHERE usernameOrEmail=?",
							   handles, usernameOrEmail);
	    }
	catch (SQLException e)
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);				
		return updateResult;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return updateResult;
	    }
    }
}

