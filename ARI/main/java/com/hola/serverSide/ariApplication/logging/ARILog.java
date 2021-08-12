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

package com.hola.serverSide.ariApplication.logging;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

/*
 * Hola! specific
 */

public class ARILog
{
    private static Logger log;
    private static DailyRollingFileAppender dailyRollingFileAppender;
    
    public static void initialize (String logFilePath)
    {
	dailyRollingFileAppender = new DailyRollingFileAppender ();
	dailyRollingFileAppender.setName ("DailyRollingFileLogger");
        dailyRollingFileAppender.setFile (logFilePath);
	// 2016-02-26 21:36:52,676 DEBUG [ProgrammaticLog4J] I am a silly man and I am ok, i am ok.
        //dailyRollingFileAppender.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        dailyRollingFileAppender.setLayout (new PatternLayout("%d{dd MMM yyyy HH:mm:ss.SSS} %-5p %l %m%n"));
        //dailyRollingFileAppender.setThreshold(Priority.INFO);
        dailyRollingFileAppender.setAppend (true);
        dailyRollingFileAppender.activateOptions ();
	
        log = Logger.getRootLogger();
        log.addAppender (dailyRollingFileAppender);
    }    
}
