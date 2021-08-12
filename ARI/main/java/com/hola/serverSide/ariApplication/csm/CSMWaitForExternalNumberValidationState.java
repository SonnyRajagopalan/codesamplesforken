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
 * Singleton for WaitForExternalNumberValidationState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;

import com.sun.jersey.api.client.ClientResponse;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForExternalNumberValidationState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForExternalNumberValidationState.class.getName ());
    // Eager initialization
    private static final CSMWaitForExternalNumberValidationState instance = new CSMWaitForExternalNumberValidationState  ();

    private CSMWaitForExternalNumberValidationState ()
    {
	setState (CSMState.WaitForExternalNumberValidation);
    }
    
    public static CSMWaitForExternalNumberValidationState getInstance ()
    {
	return instance;
    }

    @Override
    public void processExtensionToExternalCall (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processDTMFRecognizedAsExternalNumber (Call call, Event event)
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
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelRequest));
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
	String channelEventCameIn = null;
	if (call.getLastCSMEvent () == CSMEvent.FromPSTNForNewCall)
	    {
		/*
		 * Entered this codeflow because employee is making an external call from her PSTN number.
		 * In this case, the number to call is in the DTMF the caller entered
		 */
		ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;
		
		channelEventCameIn = channelDtmfReceived.getChannel ().getId ();		
	    }
	else if (call.getLastCSMEvent () == CSMEvent.ExtensionToExternalCall)
	    {
		/*
		 * Got in this codeflow because an extension dialed an external number. The external number is in 
		 */		
		StasisStart stasisStart = (StasisStart) event;
		channelEventCameIn = stasisStart.getChannel ().getId ();
	    }
	ClientResponse ringIncomingResponse = ChannelsARIAPI.ring (channelEventCameIn);

	if (ringIncomingResponse.getStatus () == 404)
	    {
		log.error ("Incoming channel {} not found for call {}; transitioning to the CleanUp (or Error?) state",
			   channelEventCameIn, call.getUUID ());
		return CSMEvent.Error;
	    }
	if (ringIncomingResponse.getStatus () == 409)
	    {
		log.error ("Incoming channel {} not found for call {}; transitioning to the CleanUp (or Error?) state",
			   channelEventCameIn, call.getUUID ());
		return CSMEvent.Error;
	    }

	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
			    " CSM external number validation state: Call " + call.getUUID () + 
			    " is in CSMEvent." + call.getLastCSMEvent () + " state");

	return CSMEvent.Success;
    }
}
