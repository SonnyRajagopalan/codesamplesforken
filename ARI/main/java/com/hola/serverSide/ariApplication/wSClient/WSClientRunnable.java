/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.wSClient;

import com.hola.serverSide.ariApplication.csm.CSMIdleState;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.ARITransactionType;

import java.lang.InterruptedException;

/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class WSClientRunnable implements Runnable
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (WSClientRunnable.class.getName ());
    private Event event;
    private final long tID; // ID of the thread

    private long getCurrentThreadID ()
    {
	return this.tID;
    }

    public WSClientRunnable (Event _event)
    {
	this.event = _event;
	this.tID   = Thread.currentThread ().getId ();
	//log.debug ("+++Thread ID " + Thread.currentThread ().getId ()+ " created for " + _event.getType () + " event");
	log.debug ("+++Thread ID " + Thread.currentThread ().getId ()+ " created for " + _event.getType () + " event");
    }

    @Override
    public void run ()
    {
	//log.debug ("---Thread ID " + getCurrentThreadID () +" begin proc." + this.event.getType () + " event");
	log.debug ("   Thread ID " + Thread.currentThread () + " begin proc." + this.event.getType () + " event");
	CSMIdleState.getInstance ().processReceivedCallEvent (event);
	//log.debug ("---Thread ID " + getCurrentThreadID () +" end proc. " + this.event.getType () + " event");
	log.debug ("---Thread ID " + Thread.currentThread () + " end proc. " + this.event.getType () + " event");
    }
}
