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
 * Singleton for WaitForIncomingSetupState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.CallerID;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import com.sun.jersey.api.client.ClientResponse;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForIncomingSetupState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForIncomingSetupState.class.getName ());
    // Eager initialization
    private static final CSMWaitForIncomingSetupState instance = new CSMWaitForIncomingSetupState  ();

    private CSMWaitForIncomingSetupState ()
    {
	setState (CSMState.WaitForIncomingSetup);
    }

    public static CSMWaitForIncomingSetupState getInstance ()
    {
	return instance;
    }

    @Override
    public void processExtensionToExtensionCall (Call call, Event event)
    {
	// TBD
	switch (getTransitionForCallContext (call, event))
	    {
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
		break;
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelRequest));
		call.processSuccess (event);
		break;
	    default:
		// Error
	    }
    }

    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	/*
	 * There is really only one way you can get here as of the trial
	 * version of the software.
	 * 
	 * 1. ring the caller's channel by using ChannelsARIAPI.
	 * 2. originate an outgoing channel by using ChannelsARIAPI
	 * 3. If 1. or 2. caused a HTTP error, transition to CSMError
	 * 4. Else, if 2. didn't work for mapping not found, then
	 *        transition to WaitForOutgoingMapping
	 *    otherwise, it worked, transition to WaitForOutgoingChannelRequest
	 */

	StasisStart stasisStart = (StasisStart) event;
	ClientResponse ringIncomingResponse = ChannelsARIAPI.ring (stasisStart.getChannel ().getId ());

	if (ringIncomingResponse.getStatus () == 404)
	    {
		log.error ("Incoming channel {} not found for call {}; transitioning to the CleanUp (or Error?) state",
			   stasisStart.getChannel ().getId (), call.getUUID ());
		return CSMEvent.Error;
	    }
	if (ringIncomingResponse.getStatus () == 409)
	    {
		log.error ("Incoming channel {} not found for call {}; transitioning to the CleanUp (or Error?) state",
			   stasisStart.getChannel ().getId (), call.getUUID ());
		return CSMEvent.Error;
	    }
	log.debug ("Thread ID " + Thread.currentThread ().getId ());
	return CSMEvent.Success;
    }
}
