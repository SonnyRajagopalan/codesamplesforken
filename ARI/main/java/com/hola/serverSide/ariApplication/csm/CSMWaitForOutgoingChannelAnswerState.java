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
 * Singleton for WaitForOutgoingChannelAnswerState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.common.ThreadUtils;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForOutgoingChannelAnswerState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForOutgoingChannelAnswerState.class.getName ());
    // Eager initialization
    private static final CSMWaitForOutgoingChannelAnswerState instance = new CSMWaitForOutgoingChannelAnswerState  ();

    private CSMWaitForOutgoingChannelAnswerState ()
    {
	setState (CSMState.WaitForOutgoingChannelAnswer);
    }
    
    public static CSMWaitForOutgoingChannelAnswerState getInstance ()
    {
	return instance;
    }

    @Override
    public void processCalleeProcessing (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processOutgoingChannelRing (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
	// Do nothing when the outgoing rings...
	// You could set a timer for a timeout....
        //process (call, event);
    }

    @Override
    public void processIncomingHangup (Call call, Event event)
    {
	call.changeState (getStateForStateName (CSMState.WaitForIncomingHangupProcessing));
	call.processIncomingHangup (event);
    }

    @Override
    public void processOutgoingHangup (Call call, Event event)
    {
	call.changeState (getStateForStateName (CSMState.WaitForOutgoingHangupProcessing));
	call.processOutgoingHangup (event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		log.debug ("Call {}: Stacktrace=: {}", call.toString (), ThreadUtils.getThreadIDedStackTraceString ());
		call.changeState (getStateForStateName (CSMState.WaitForMixingBridgeCreate));
		call.getCurrentState ().processSuccess (call, event);
		break;
	    // case Timeout:
	    // 	call.changeState (getStateForStateName (CSMState.WaitForOutgoingHangupProcessingForError));
	    // 	call.getCurrentState ().processTimeout (call, event);
	    // 	break;
	    default:
		// Error event?
	    }
    }
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	// Nothing for now
	// Here, the event itself is processed and then the type of event is deduced
	return CSMEvent.Success;
    }
}
