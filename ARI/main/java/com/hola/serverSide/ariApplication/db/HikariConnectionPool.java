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
import java.sql.SQLException;
import java.sql.Statement;

import java.lang.IllegalArgumentException;
import java.io.IOException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
/*
  Logging
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.init.ARIInit;

public class HikariConnectionPool
{
    private static final Logger log = LoggerFactory.getLogger (HikariConnectionPool.class.getName());
    // Eager initialization -- we do expect to use the connections, afterall.
    private static HikariDataSource hikariDataSource;
    private static HikariConnectionPool hikariCP = new HikariConnectionPool ();
    //private static volatile HikariConnectionPool hikariCP; // = new HikariConnectionPool ();

    private HikariConnectionPool () // We don't want any other object creating this pool
    {
	HikariConfig config;

	/*
	  Start using config file
	 */
	try	    
	    {
		config = new HikariConfig (ARIInit.getDBPropertiesFilePath ());
		log.debug ("Connecting to hikari data source...");
		hikariDataSource = new HikariDataSource (config);
	    }
	catch (IllegalArgumentException e)
	    {
		e.printStackTrace ();
	    }
	catch (RuntimeException e)
	    {
		e.printStackTrace ();
	    }
	/*
	  End using config file
	 */
    }

    public static HikariConnectionPool getInstance()
    {
	// 06/18/2014. Moved to Eager initialization after several bugs were found in the lazy initialization below.
	// If lazy initialization is needed later, will try the holder pattern
	// see http://antrix.net/posts/2012/java-lazy-initialization/
	// and JAVA concurrency in practice (Brian Goetz).
	// if (hikariCP == null)
	//     {
	// 	synchronized (HikariConnectionPool.class)
	// 	    {
	// 		if (hikariCP == null)
	// 		    {
	// 			hikariCP = new HikariConnectionPool ();
	// 		    }
	// 	    }
	//     }
	return hikariCP;
    }

    public static HikariDataSource getDataSource ()
    {
	return hikariDataSource;
    }
}
