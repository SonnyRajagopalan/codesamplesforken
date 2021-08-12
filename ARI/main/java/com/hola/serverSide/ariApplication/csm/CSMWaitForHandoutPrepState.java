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
 * Singleton for WaitForHandoutPrepState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.sun.jersey.api.client.ClientResponse;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola! specific.
 */
import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;

public class CSMWaitForHandoutPrepState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForHandoutPrepState.class.getName ());
    // Eager initialization
    private static final CSMWaitForHandoutPrepState instance = new CSMWaitForHandoutPrepState  ();

    private CSMWaitForHandoutPrepState ()
    {
	setState (CSMState.WaitForHandoutPrep);
    }
    
    public static CSMWaitForHandoutPrepState getInstance ()
    {
	return instance;
    }

    @Override
    public void processFromPSTNForExistingCall (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processSIPLegHandInForActiveCallRequested (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForAddToActiveBridge));
		call.processSuccess (event);
		break;
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
		break;
	    default:
		// Error event?
	    }
    }
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	/*	  
	  E.g.:
	  {"type":"StasisStart","application":"psa","timestamp":1456017897158,
	  "args":["did","+13022290507"],"channel":{"accountcode":"","caller":{"name":"","number":"+13022290507"},
	  "connected":{"name":"","number":""},"creationtime":1456017897157,
	  "dialplan":{"context":"from-external","exten":"+17812096264","priority":2},"id":"1456017897.460",
	  "language":"en","name":"PJSIP/twilio-siptrunk-000000d7","state":"Ring"},"replace_channel":null}
		
	 * First, answer the call
	 */
	StasisStart stasisStart = (StasisStart) event;
	ClientResponse answerResponse = ChannelsARIAPI.answer (stasisStart.getChannel ().getId ());

	if (answerResponse.getStatus () == 404)
	    {
		return CSMEvent.Error;
	    }
	else if (answerResponse.getStatus () == 409)
	    {
		return CSMEvent.Error;
	    }
	// Here the PSTN caller and Asterisk are connected--we still have to add this call to the bridge
	// in the next state

	return CSMEvent.Success;	
    }
}
