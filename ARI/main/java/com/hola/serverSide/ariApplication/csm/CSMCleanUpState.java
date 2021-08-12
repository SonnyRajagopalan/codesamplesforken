/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
/*
 * 10.21.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! Call State Machine
 *
 * Singleton for CleanUpState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CallDB;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.common.ThreadUtils;
import com.hola.serverSide.ariApplication.common.Color;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMCleanUpState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMCleanUpState.class.getName ());
    // Eager initialization
    private static final CSMCleanUpState instance = new CSMCleanUpState  ();

    private CSMCleanUpState ()
    {
	setState (CSMState.CleanUp);
    }
    
    public static CSMCleanUpState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}: cleaning up call and removing it from the callDB", 
		   call.getUUID (), event.getType ());
	call.logObject ();
	System.out.println (Color.Red + "Cleaned up the following call: " + Color.End);
	call.printObject ();
	log.debug (ThreadUtils.getThreadIDedStackTraceString ());
	/*
	 * First, remove the call from the DB
	 */
	// if (call.getLastCSMEvent () != CSMEvent.OutgoingChannelHungup)
	//     {
	// 02-24-2016. NEEDS EXTENSIVE TESTING> THIS CAN CAUSE BIG ISSUES
	// CHECK CHECK CHECK
		CallDB.removeACallFromDB (call.getUUID ());
	//     }
	// else
	//     {
	// 	log.debug ("Call {}: not removed from DB as OutgoingChannelHungup");
	//     }
    }

}
