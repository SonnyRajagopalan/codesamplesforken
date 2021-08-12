package com.hola.serverSide.ariApplication.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/*
  Hola specific
*/
import com.hola.serverSide.ariApplication.db.beans.AccountPolicy;
import com.hola.serverSide.ariApplication.policy.Policy;
import com.hola.serverSide.ariApplication.policy.PolicyHelper;

public class AccountPoliciesHelper
{
    private static final Logger log = LoggerFactory.getLogger (AccountPoliciesHelper.class.getName ());

    private static final ObjectMapper objectMapper = new ObjectMapper ();

    public static int putAccountPolicy (AccountPolicy accountPolicy)
    {
	int putAccountHandleReturnCode = 0;
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	try
	    {
		/*

		  mysql> describe accountPolicies;
		  +-----------------+---------------+------+-----+---------+-------+
		  | Field           | Type          | Null | Key | Default | Extra |
		  +-----------------+---------------+------+-----+---------+-------+
		  | usernameOrEmail | varchar(100)  | NO   | PRI | NULL    |       |
		  | extension       | int(10)       | NO   | MUL | NULL    |       |
		  | policies        | varchar(2000) | NO   |     | NULL    |       |
		  +-----------------+---------------+------+-----+---------+-------+

		  mysql> insert into  accountPolicies (usernameOrEmail, extension, policies)
		  VALUES  ("ddas@pacifi.net",  6002,  "SILLY_POLICIES")  ON  DUPLICATE  KEY
		  UPDATE policies=VALUES (policies);

		  Query OK, 1 row affected (0.02 sec)
		  
		  mysql> select * from accountPolicies;
		  +-----------------+-----------+---------------+
		  | usernameOrEmail | extension | policies      |
		  +-----------------+-----------+---------------+
		  | ddas@pacifi.net |      6002 | SILLY_POLICIES|
		  +-----------------+-----------+---------------+
		  1 row in set (0.02 sec)
		  
		  mysql> insert into  accountPolicies (usernameOrEmail, extension, handles)
		  VALUES  ("ddas@pacifi.net", 6002,  "SILLIER_HANDLES")  ON DUPLICATE  KEY
		  UPDATE handles=VALUES (handles);

		  Query OK, 2 rows affected (0.02 sec)
		  
		  mysql> select * from accountPolicies;
		  +-----------------+-----------+-----------------+
		  | usernameOrEmail | extension | handles         |
		  +-----------------+-----------+-----------------+
		  | ddas@pacifi.net |      6002 | SILLIER_HANDLES |
		  +-----------------+-----------+-----------------+
		  1 row in set (0.02 sec)


		*/
		QueryRunner putAccountPolicyForUserQuery = new QueryRunner ();
		putAccountHandleReturnCode = 
		    putAccountPolicyForUserQuery.update (connectionFromHikariCP,					  
							  "INSERT INTO accountPolicies (usernameOrEmail, extension, policies) " +
							 " VALUES (?, ?, ?)  ON DUPLICATE KEY" +
							  " UPDATE policies=VALUES (policies)",
							  new Object [] 
							  {accountPolicy.getUsernameOrEmail (), 
							   accountPolicy.getExtension (), 
							   accountPolicy.getPolicies ()});
		
		log.debug ("The result of this transaction is {}", putAccountHandleReturnCode);
		return putAccountHandleReturnCode;
	    }
	catch (SQLException e)
	    {
		log.error ("Error putting user {}'s account handles into accountPolicies table in pacifi DB", 
			   accountPolicy.getUsernameOrEmail ());
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

    public static AccountPolicy getAccountPolicyByUsernameOrEmail (String usernameOrEmail)
    {
	Connection connectionFromHikariCP = HikariHelper.getADBConnection ();
	AccountPolicy accountPolicy = null;
	try
	    {
		QueryRunner getAccountPolicyQuery = new QueryRunner ();
		accountPolicy = 
		    (AccountPolicy) getAccountPolicyQuery.query (connectionFromHikariCP, 
								 "SELECT * FROM accountPolicies WHERE usernameOrEmail=?",
								 new BeanHandler <AccountPolicy> (AccountPolicy.class), 
								 usernameOrEmail);
		log.debug ("Retrieved {}", accountPolicy.toJsonString ());
	    }
	catch (SQLException e)
	    {
		log.error ("Error running SQl query for fetching account handles for {} from accountPolicies table in pacifi DB", 
			   usernameOrEmail);
		DbUtils.closeQuietly (connectionFromHikariCP);
		return null;
	    }
	finally
	    {
		DbUtils.closeQuietly (connectionFromHikariCP);
		return accountPolicy;
	    }
    }

    public static List <Policy> getPolicyListForUsernameOrEmail (String usernameOrEmail)
    {
	AccountPolicy accountPolicy = getAccountPolicyByUsernameOrEmail (usernameOrEmail);

	return PolicyHelper.getPoliciesFromPolicyString (accountPolicy.getPolicies ());
    }
}
