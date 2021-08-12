/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.init;
/*
 * 10.21.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! initialization
 */

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola! specific
 */

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.HTTPAPIForARI;
import com.hola.serverSide.ariApplication.wSClient.WSClientWrapper;
import com.hola.serverSide.ariApplication.db.HikariConnectionPool;
import com.hola.serverSide.ariApplication.logging.CallEventLogger;
import com.hola.serverSide.ariApplication.logging.ARILog;

public class ARIInit
{
    // Need to read configuration from /opt/hola/config/config.properties
    // 1. Need a WS interface to server
    //    (will instantiate an HTTP connection as and when necessary)
    // 2. Need 

    // Config properties sample:
    // 1. First time config flag (true/false). If true, will add contacts to DB
    // 2. WS server IP address
    // 3. NUM_THREADS for incoming WS messages
    // 4. Database information
    // Logging
    private static final Logger log = LoggerFactory.getLogger (ARIInit.class.getName ());

    private static final String ARIWSServerURIString = "ws://localhost:8088/ari/events?api_key=asterisk:asterisk&app=psa";

    private static  int                  numberOfThreads;
    private static  String               ipAddressOfServer;
    private static  int                  port;
    private static  String               dbPropertiesFilePath;
    private static  String               ariUsername;
    private static  String               ariPassword;
    private static  String               logFilePath;
    private static  String               callLogsFilePath;
    private static  boolean              extensionLengthCanBeVary;
    private static  int                  extensionLength;
    private static  HikariConnectionPool hikariCP;

    public ARIInit (String _config)
    {
	getConfigFromProperties (_config);
    }

    public static int getNumberOfThreads ()
    {
	return numberOfThreads;
    }

    public static String getWebSocketURI ()
    {
	return "ws://" + ipAddressOfServer + ":" + port + "/ari/events?api_key=" +
	    ariUsername + ":" + ariPassword;
    }

    public static String getHTTPARIURI ()
    {
	return "http://" + ipAddressOfServer + ":" + port + "/ari";
    }

    public static String getARIUsername ()
    {
	return ariUsername;
    }

    public static String getARIPassword ()
    {
	return ariPassword;
    }

    public static String getLogFilePath ()
    {
	return logFilePath;
    }

    public static String getCallLogsFilePath ()
    {
	return callLogsFilePath;
    }

    public static String getDBPropertiesFilePath ()
    {
	return dbPropertiesFilePath;
    }

    public void getConfigFromProperties (String config)
    {
        Properties configProperties = new Properties();
        InputStream configFile = null;

        try {
                configFile = new FileInputStream (config);
                configProperties.load (configFile);
                numberOfThreads      = Integer.parseInt (configProperties.getProperty ("maxThreads"));
		ipAddressOfServer    = configProperties.getProperty ("ipAddressOfServer");
		port                 = Integer.parseInt (configProperties.getProperty ("port"));
		ariUsername          = configProperties.getProperty ("ariUsername");
		ariPassword          = configProperties.getProperty ("ariPassword");
		logFilePath          = configProperties.getProperty ("logFilePath");
		callLogsFilePath     = configProperties.getProperty ("callLogsFilePath");
		//firstTimeConfig      = configProperties.getProperty ("firstTimeConfig");
		//contactsFilePath     = configProperties.getProperty ("contactsFilePath");
		//policiesFilePath     = configProperties.getProperty ("policiesFilePath");
		dbPropertiesFilePath = configProperties.getProperty ("dbPropertiesFilePath");

		// if (firstTimeConfig)
		//     {
		// 	log.info ("This is first time configuration for this app. This means DB set " + 
		// 		  "up, etc. will likely be performed.");
		//     }
		// else
		//     {
		// 	// Nothing
		//     }

		log.info ("Read in config file from " + config);
		log.info ("Number of threads (max) to use: " + numberOfThreads);
		log.info ("IP address of server to use: " + ipAddressOfServer);
		log.info ("Port on server to use: " + port);
		log.info ("ARI username/password: " + ariUsername + "/" + ariPassword);
		log.info ("The log file path is " + logFilePath);
		log.info ("The call log file path is " + callLogsFilePath);
		log.info ("The DB properties file path is " + dbPropertiesFilePath);

	}
	catch (IOException e)
	    {
	    }
	finally
	    {
		if (configFile != null) 
		    {
                        try 
			    {
                                configFile.close();
			    } 
			catch (IOException e) 
			    {
                                e.printStackTrace();
			    }
		    }
		connectToWSInterface ();
	    }
    }
    
    public void connectToWSInterface ()
    {
	//String ARIWSServerURIString = "ws://localhost:8088/ari/events?api_key=asterisk:asterisk&app=psa";
	String ARIWSServerURIString = getWebSocketURI () + "&app=psa";
	WSClientWrapper wsClientWrapper = new WSClientWrapper (ARIWSServerURIString);
    }

    public void setUpContacts ()
    {
    }

    public void connectToDBs ()
    {
	hikariCP = HikariConnectionPool.getInstance ();
    }

    
    public static void main (String[] args)
    {
	log.info ("Getting the ARI backend started...");
	String propertiesFilePath = "/opt/hola/config/base/config.properties";

	if (args.length != 0)
	    {
		propertiesFilePath = args [0];
	    }

	log.debug ("Reading config from " + propertiesFilePath);

	ARIInit ariInitialization = new ARIInit (propertiesFilePath);
	HTTPAPIForARI httpAPIARI  = new HTTPAPIForARI (getHTTPARIURI (), getARIUsername (), getARIPassword ());
	//Call.initializeCallDB ();
	CallEventLogger.initialize (callLogsFilePath);
	ARILog.initialize (logFilePath);

	while (1==1)
	    {
	    }
    }
}
