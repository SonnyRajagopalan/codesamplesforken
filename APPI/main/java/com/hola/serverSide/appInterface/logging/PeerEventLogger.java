/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 *
 * 02.26.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! Call Event Logger
 *
 * Call event logging
 */

package com.hola.serverSide.appInterface.logging;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

/*
 * Hola! specific
 */
import com.hola.serverSide.appInterface.peer.APPIPeerState;

public class PeerEventLogger
{
    private static Logger log;
    private static RollingFileAppender rollingFileAppender;
    private static PeerEventLogger peerEventLogger = new PeerEventLogger ();

    public PeerEventLogger ()
    {
	rollingFileAppender = new RollingFileAppender ();
	rollingFileAppender.setName ("RollingFileLogger");
        rollingFileAppender.setFile ("/opt/hola/appi/logs/peerEvent.log");
	// 2016-02-26 21:36:52,676 DEBUG [ProgrammaticLog4J] I am a silly man and I am ok, i am ok.
        //rollingFileAppender.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        rollingFileAppender.setLayout (new PatternLayout("%d %m%n"));
        //rollingFileAppender.setThreshold(Priority.INFO);
        rollingFileAppender.setAppend (true);
        rollingFileAppender.activateOptions ();
	
        log = Logger.getLogger (PeerEventLogger.class.getName ());
        log.addAppender (rollingFileAppender);
    }
    
    public static void peerLog (APPIPeerState peerState, String message)
    {
	log.debug ("[" + peerState.getUUID () + "] " + message);
    }
}
