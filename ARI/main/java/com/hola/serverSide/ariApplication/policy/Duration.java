/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Duration
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (Duration.class.getName ());

    private final LocalTime start;
    private final LocalTime end;

    public Duration (String _start, String _end)
    {
	this.start = LocalTime.parse (_start, DateTimeFormatter.ofPattern ("HHmm"));
	this.end   = LocalTime.parse (_end,   DateTimeFormatter.ofPattern ("HHmm"));
    }

    public Duration (LocalTime _start, LocalTime _end)
    {
	this.start = _start;
	this.end   = _end;
    }

    public LocalTime getStart ()
    {
	return this.start;
    }

    public LocalTime getEnd ()
    {
	return this.end;
    }

    public boolean timeInDuration (LocalTime time)
    {
	if (start.isBefore (end))
	    {
		// Don't flip: the hours are for the same day. E.g. between 4:00PM and 5PM the same day [1600, 1700]
		if (time.isAfter (this.start) && time.isBefore (this.end))
		    {
			log.debug ("Time in duration....");
			return true;
		    }
		else
		    {
			log.debug ("Time NOT in duration: time.isAfter (this.start): " + time.isAfter (this.start) +
					    " and time.isBefore (this.end): " + time.isBefore (this.end));
			return false;
		    }
	    }
	else
	    {
		// Flip: the hours are on different dates. E.g., between 8PM and 8AM [2000, 0800]

		if (time.isAfter (this.start) || time.isBefore (this.end))
		    {
			log.debug ("Time in duration....");
			return true;
		    }
		else
		    {
			log.debug ("Time NOT in duration: time.isAfter (this.start): " + time.isAfter (this.start) +
					    " and time.isBefore (this.end): " + time.isBefore (this.end));
			return false;
		    }

	    }
    }
    
    public void print ()
    {
	log.debug ("\t[" + this.start + ", " + this.end + "]");
    }
}
