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
 * Singleton for WaitForSIPChannelSetupState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForSIPChannelSetupState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForSIPChannelSetupState.class.getName ());
    // Eager initialization
    private static final CSMWaitForSIPChannelSetupState instance = new CSMWaitForSIPChannelSetupState  ();

    private CSMWaitForSIPChannelSetupState ()
    {
	setState (CSMState.WaitForSIPChannelSetup);
    }
    
    public static CSMWaitForSIPChannelSetupState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processError (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }
    
    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForActiveCallDBLookup));
		call.getCurrentState ().processSuccess (call, event);
		break;
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.getCurrentState ().processError (call, event);
		break;
	    default:
		// Error event?
	    }
    }
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	// Nothing for now
	// Here, the event itself is processed and then the type of event is deduced
	return CSMEvent.UNKNOWN;
    }
}
