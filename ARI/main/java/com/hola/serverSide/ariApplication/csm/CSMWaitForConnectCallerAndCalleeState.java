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
 * Singleton for WaitForConnectCallerAndCalleeState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
//import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;

// import java.io.IOException;
// import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForConnectCallerAndCalleeState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForConnectCallerAndCalleeState.class.getName ());
    // Eager initialization
    private static final CSMWaitForConnectCallerAndCalleeState instance = new CSMWaitForConnectCallerAndCalleeState  ();

    private CSMWaitForConnectCallerAndCalleeState ()
    {
	setState (CSMState.WaitForConnectCallerAndCallee);
    }
    
    public static CSMWaitForConnectCallerAndCalleeState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForUpdateActiveCallDB));
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
 	if ((call.getLastCSMEvent () != CSMEvent.FromPSTNForExistingCall) &&
	    (call.getLastCSMEvent () != CSMEvent.SIPLegHandInForActiveCallRequested)) // ONLY for fresh new calls
	    //if (call.getLastCSMEvent () != CSMEvent.FromPSTNForExistingCall)
	    {
		/*
		 * First, mix the caller channel and the callee channels in the bridge
		 */
		String callerChannelID = call.getFirstCallerChannelID ();
		String calleeChannelID = call.getFirstCalleeChannelID ();
		String bridgeID        = call.getBridge ().getId ();
		String query = "?channel=" + callerChannelID + "," + calleeChannelID;

		ClientResponse addChannelResponse = BridgesARIAPI.addChannel (bridgeID, query);

		if (addChannelResponse.getStatus () == 400)
		    {
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 404)
		    {
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 409)
		    {
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 422)
		    {
			return CSMEvent.Error;
		    }
	
		/*
		 * Next, answer the call for the caller.
		 */
		ClientResponse answerIncomingResponse = ChannelsARIAPI.answer (call.getFirstCallerChannelID ());
		
		if (answerIncomingResponse.getStatus () == 404)
		    {
			return CSMEvent.Error;
		    }
		else if (answerIncomingResponse.getStatus () == 409)
		    {
			return CSMEvent.Error;
		    }
		// Here the caller and callee are connected! Yipee!
	    }
	else
	    {
		// For handout: already mixed the (new) channel from the PSTN leg into the bridge in 
		//     CSMWaitForAddToActiveBridgeState
		// For handin: already mixed the (new) channel from the SIP leg into the bridge in 
		//     CSMWaitForAddToActiveBridgeState
	    }

	//ActiveCallsHelper.updateActiveCallWithNewCalleeInfo (call.getUUID (), calleeChannelID, "caller handle from event");
	return CSMEvent.Success;
    }
}
