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
 * Singleton for ErrorState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import java.util.Map;
import java.util.Iterator;
import com.sun.jersey.api.client.ClientResponse;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CallDB;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.Channel;
import com.hola.serverSide.ariApplication.ari.Event;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMErrorState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMErrorState.class.getName ());
    // Eager initialization
    private static final CSMErrorState instance = new CSMErrorState  ();

    private CSMErrorState ()
    {
	setState (CSMState.Error);
    }
    
    public static CSMErrorState getInstance ()
    {
	return instance;
    }

    @Override
    public void processError (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
	call.printObject ();

	/*
	 * Remove all call related resources: bridges, channels and state information in backend
	 */

	
	/*
	 * First, delete bridge
	 */
	if (call.getBridge () != null)
	    {
		ClientResponse bridgeHangupResponse = BridgesARIAPI.destroy (call.getBridge ().getId ());
		
		if (bridgeHangupResponse.getStatus () == 404)
		    {
			log.error ("Bridge with ID {} not found for call {}", call.getBridge ().getId (), call.getUUID ());
		    }
	    }
	/*
	 * Next, delete all channels associated with this call
	 */
	ClientResponse channelHangupResponse;

	Iterator callerChannelIterator = call.getCallerChannels ().entrySet ().iterator ();
	while (callerChannelIterator.hasNext ())
	    {
		
		Map.Entry callerChannelEntry = (Map.Entry) callerChannelIterator.next ();
		Channel callerChannel = (Channel) callerChannelEntry.getValue ();

		channelHangupResponse =  ChannelsARIAPI.hangup (callerChannel.getId (), "?normal");
	
		if (channelHangupResponse.getStatus () == 400)
		    {
			log.error ("Invalid reason for caller channel {} hang up for call {}", 
				   callerChannel.getId (), call.getUUID ());
		    }
		else if (channelHangupResponse.getStatus () == 404)
		    {
			log.error ("Caller channel {} not found: cannot hang up for call {}", 
				   callerChannel.getId (), call.getUUID ());
		    }
		
		//call.removeFromCallerChannels (callerChannel.getId ());
	    }

	Iterator calleeChannelIterator = call.getCalleeChannels ().entrySet ().iterator ();
	while (calleeChannelIterator.hasNext ())
	    {
		
		Map.Entry calleeChannelEntry = (Map.Entry) calleeChannelIterator.next ();
		Channel calleeChannel = (Channel) calleeChannelEntry.getValue ();

		channelHangupResponse =  ChannelsARIAPI.hangup (calleeChannel.getId (), "?normal");
	
		if (channelHangupResponse.getStatus () == 400)
		    {
			log.error ("Invalid reason for callee channel {} hang up for call {}", 
				   calleeChannel.getId (), call.getUUID ());
		    }
		else if (channelHangupResponse.getStatus () == 404)
		    {
			log.error ("Callee channel {} not found: cannot hang up for call {}", 
				   calleeChannel.getId (), call.getUUID ());
		    }
		
		//call.removeFromCalleeChannels (calleeChannel.getId ());
	    }

	/*
	 * Last, remove the call from the callDB
	 */
	CallDB.removeACallFromDB (call.getUUID ());
    }
}
