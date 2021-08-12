/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.common;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Calendar;
import java.text.ParseException;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils
{
    private static final Logger log = LoggerFactory.getLogger (TimeUtils.class.getName());
    

    // public static Timestamp getTimestampFromHTMLDateAndHTMLTimeStrings (String date, String time)
    // {
    // }
    
    // public static Timestamp getTimestampFromJavaUtilDate (java.util.Date javaUtilDate)
    // {
    // }

    // public static Timestamp getTimestampFromJavaSQLDate (java.sql.Date javaSqlDate)
    // {
    // }

    // public static java.sql.Date getJavaSQLDateWithDateOnlyFromJavaUtilDate (java.sql.Date javaSqlDate)
    // {
    // }

    // public static java.sql.Date getJavaSQLDateFromJavaUtilDate (java.sql.Date javaSqlDate)
    // {
    // }

    // public static java.util.Date getJavaUtilDateFromJavaSQLDate (java.sql.Date javaSqlDate)
    // {
    // }



    public static Timestamp getATimestampXSecondsLater (Timestamp t, int seconds)
    {
	Calendar cal = Calendar.getInstance ();
	cal.setTimeInMillis (t.getTime());
	cal.add (Calendar.SECOND, seconds);
	return new Timestamp (cal.getTime().getTime());
    }

    public static Date getADateObjectFromHTMLDateAndTimeStrings (String dateString, String timeString)
    {
	SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
	Date convertedDateObject = null;

	try
	    {
		convertedDateObject = df.parse (dateString + " " + timeString);
	    }
	catch (ParseException e)
	    {
		e.printStackTrace ();
	    }
	return convertedDateObject;
    }

    public static Date getADateObjectFromHTMLDateString (String dateString)
    {
	SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
	Date convertedDateObject = null;

	try
	    {
		convertedDateObject = df.parse (dateString);
	    }
	catch (ParseException e)
	    {
		e.printStackTrace ();
	    }
	return convertedDateObject;
    }

    public static Date getADateObjectFromDateString (String dateString)
    {
	DateFormat df = DateFormat.getDateTimeInstance (DateFormat.SHORT, DateFormat.LONG);
	Date convertedDateObject = null;

	try
	    {
		convertedDateObject = df.parse (dateString);
	    }
	catch (ParseException e)
	    {
		e.printStackTrace ();
	    }
	return convertedDateObject;
    }
    
    public static boolean currentDatetimeIsPast (Date someDatetime)
    {
	Date currentDatetime = new Date ();
	
	if (currentDatetime.compareTo (someDatetime) > 0)
	    {
		return true;
	    }
	else
	    {
		return false;
	    }
    }

    public static boolean datetimesAreTheSame (Date datetime1, Date datetime2)
    {
	if (datetime1.compareTo (datetime2) == 0)
	    {
		log.info ("Two dates are the same:" + datetime1 + " and " + datetime2);
		return true;
	    }
	else
	    {
		log.info ("Two dates are not the same:" + datetime1 + " and " + datetime2);
		return false;
	    }
    }
    
    public static String convertDateObjectToString (Date datetime)
    {
	return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM).format(datetime);
    }

    public static Date getCurrentTime ()
    {
	return new Date ();
    }
}
