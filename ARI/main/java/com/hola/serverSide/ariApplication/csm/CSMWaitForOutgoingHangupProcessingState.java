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
 * Singleton for WaitForOutgoingHangupProcessingState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.sun.jersey.api.client.ClientResponse;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.StasisEnd;
import com.hola.serverSide.ariApplication.ari.ChannelDestroyed;
import com.hola.serverSide.ariApplication.ari.Event;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForOutgoingHangupProcessingState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForOutgoingHangupProcessingState.class.getName ());
    // Eager initialization
    private static final CSMWaitForOutgoingHangupProcessingState instance = new CSMWaitForOutgoingHangupProcessingState  ();

    private CSMWaitForOutgoingHangupProcessingState ()
    {
	setState (CSMState.WaitForOutgoingHangupProcessing);
    }
    
    public static CSMWaitForOutgoingHangupProcessingState getInstance ()
    {
	return instance;
    }

    @Override
    public void processOutgoingHangup (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		// Here, you should also hang up the incoming channel
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
	/*
	 * First, delete bridge
	 */
	if (call.getBridge () != null)
	    {
		ClientResponse bridgeHangupResponse = BridgesARIAPI.destroy (call.getBridge ().getId ());
		
		if (bridgeHangupResponse.getStatus () == 404)
		    {
			log.error ("Bridge with ID {} not found for call {}", call.getBridge ().getId (), call.getUUID ());
			return CSMEvent.Error;
		    }
		else
		    {
			log.debug ("Bridge deleted for call {}", call.getUUID ());
		    }
	    }
	/* 
	 * Next, hangup the outgoing (remember, you are in CSMIncoming...
	 */

	String outgoingChannelThatHungup="";
	if (event instanceof StasisEnd)
	    {
		StasisEnd stasisEnd = (StasisEnd) event;
		outgoingChannelThatHungup = stasisEnd.getChannel ().getId ();
	    }
	else if (event instanceof ChannelDestroyed)
	    {
		ChannelDestroyed channelDestroyed = (ChannelDestroyed) event;
		outgoingChannelThatHungup = channelDestroyed.getChannel ().getId ();
	    }

	String incomingChannelToHangup   = call.getFirstCallerChannelID ();
	log.debug ("Outgoing channel that just hung up {}, and Incoming channel to hangup {}", outgoingChannelThatHungup,
		   incomingChannelToHangup);

	if (!incomingChannelToHangup.equals (""))
	    {
		ClientResponse hangupIncomingChannelResponse = ChannelsARIAPI.hangup (incomingChannelToHangup, "?normal");	
		
		if (hangupIncomingChannelResponse.getStatus () == 400)
		    {
			log.error ("Invalid reason for incoming channel {} hang up for call {}", 
				   incomingChannelToHangup, call.getUUID ());
			return CSMEvent.Error;
		    }
		else if (hangupIncomingChannelResponse.getStatus () == 404)
		    {
			log.error ("Incoming channel {} not found: cannot hang up for call {} (probably already hung up)", 
				   incomingChannelToHangup, call.getUUID ());
			// return CSMEvent.Error; // Previous to the big deadlock bug of Feb. 2016
			return CSMEvent.Success;
		    }
		else
		    {
			log.debug ("Incoming channel {} has been hung up in OutgoingHangupProcessing", incomingChannelToHangup);
		    }
		
		call.removeFromCallerChannels (incomingChannelToHangup);
		return CSMEvent.Success;
	    }

	return CSMEvent.Error;
    }
}
