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
 * Singleton for WaitForIncomingHangupProcessingState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.StasisEnd;
import com.hola.serverSide.ariApplication.ari.Event;


import com.sun.jersey.api.client.ClientResponse;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForIncomingHangupProcessingState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForIncomingHangupProcessingState.class.getName ());
    // Eager initialization
    private static final CSMWaitForIncomingHangupProcessingState instance = new CSMWaitForIncomingHangupProcessingState  ();

    private CSMWaitForIncomingHangupProcessingState ()
    {
	setState (CSMState.WaitForIncomingHangupProcessing);
    }
    
    public static CSMWaitForIncomingHangupProcessingState getInstance ()
    {
	return instance;
    }

    @Override
    public void processIncomingHangup (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		// Here, you should also hang up the outgoing channel
		call.changeState (getStateForStateName (CSMState.CleanUp));
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
	StasisEnd stasisEnd = (StasisEnd) event;
	
	/*
	 * First, delete bridge
	 */
	if (call.getBridge () != null)
	    {
		ClientResponse bridgeHangupResponse = BridgesARIAPI.destroy (call.getBridge ().getId ());
		
		if (bridgeHangupResponse.getStatus () == 404)
		    {
			log.error ("Bridge with ID {} not found for call {}", call.getBridge ().getId (), call.getUUID ());
			// Maybe the StasisEnd event of the other leg removed it. 02-17-2016
			//return CSMEvent.Error;
		    }
		else
		    {
			log.debug ("Bridge deleted for call {}", call.getUUID ());
		    }
	    }

	/* 
	 * Next, hangup the outgoing (remember, you are in CSMIncoming...
	 */

	String incomingChannelThatHungup = stasisEnd.getChannel ().getId ();
	String outgoingChannelToHangup   = call.getFirstCalleeChannelID ();
	
	if (!outgoingChannelToHangup.equals (""))
	    {
		ClientResponse hangupOutgoingChannelResponse = ChannelsARIAPI.hangup (outgoingChannelToHangup, "?normal");

		if (hangupOutgoingChannelResponse.getStatus () == 400)
		    {
			log.error ("Invalid reason for outgoing channel {} hang up for call {}", 
				   outgoingChannelToHangup, call.getUUID ());
			// Maybe the StasisEnd event of the other leg removed it. 02-17-2016
			//return CSMEvent.Error;
		    }
		else if (hangupOutgoingChannelResponse.getStatus () == 404)
		    {
			log.error ("Outgoing channel {} not found: cannot hang up for call {}", 
				   outgoingChannelToHangup, call.getUUID ());
			// Maybe the StasisEnd event of the other leg removed it. 02-17-2016
			//return CSMEvent.Error;
		    }
		else
		    {
			log.debug ("Outgoing channel {} has been hung up in IncomingHangupProcessing", outgoingChannelToHangup);
		    }
		
		call.removeFromCalleeChannels (outgoingChannelToHangup);
		return CSMEvent.Success;
	    }
	else
	    {
		log.debug ("For call {}, outgoing channel was not available (empty channel ID) to hangup", call.getUUID ());
	    }
	//return CSMEvent.Error;
	return CSMEvent.Success; // Not having an outgoing channel to hangup is not an error.
    }
}
