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
 * Singleton for WaitForUpdateActiveCallDBState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.sun.jersey.api.client.ClientResponse;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.db.ActiveHandoversHelper;
import com.hola.serverSide.ariApplication.db.beans.ActiveHandover;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.beans.ActiveCall;
import com.hola.serverSide.ariApplication.logging.CallEventLogger;

public class CSMWaitForUpdateActiveCallDBState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForUpdateActiveCallDBState.class.getName ());
    // Eager initialization
    private static final CSMWaitForUpdateActiveCallDBState instance = new CSMWaitForUpdateActiveCallDBState  ();

    private CSMWaitForUpdateActiveCallDBState ()
    {
	setState (CSMState.WaitForUpdateActiveCallDB);
    }
    
    public static CSMWaitForUpdateActiveCallDBState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		//addTheCallToActiveCallDB (call);
		call.changeState (getStateForStateName (CSMState.ActiveCallState));
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
	synchronized (call)
	    {
		StasisStart stasisStart = (StasisStart) event;

		if ((call.getLastCSMEvent () == CSMEvent.FromPSTNForExistingCall) ||
		    (call.getLastCSMEvent () == CSMEvent.SIPLegHandInForActiveCallRequested))
		    {
			ClientResponse removeChannelFromBridgeResponse;
			String IDOfChannelToDrop = "TBD";
			ActiveCall activeCall = ActiveCallsHelper.getActiveCallByUUID (call.getUUID ());

			/*
			  First, get the information from the activeHandover
			*/
			ActiveHandover activeHandover = 
			    ActiveHandoversHelper.getActiveHandoverByUUIDAndChannelID (call.getUUID (), 
										       stasisStart.getChannel ().getId ());
			CallEventLogger.callLog (call, "Processing active handover " + activeHandover.toJsonString ());

			if (activeHandover.getCallerOrCallee ().equals ("caller"))
			    {
				IDOfChannelToDrop = activeCall.getCallerChannel ();
				//synchronized (call)
				//{
				call.removeFromCallerChannels (IDOfChannelToDrop);
				// It is OK to do this here, rather than wait to make sure that the channels were indeed removed, 
				// because as of this moment, we have "made" the new call. If there is any error here, the 
				// call has to drop, and we'll return CSMEvent.Error
				ActiveCallsHelper.updateActiveCallWithCallerInfo (call.getUUID (), 
										  activeHandover.getCallerChannel (),
										  activeHandover.getCallerHandle ());
				call.addToCallerChannels (stasisStart.getChannel ());
				//}
			    }
			else if (activeHandover.getCallerOrCallee ().equals ("callee"))
			    {
				IDOfChannelToDrop = activeCall.getCalleeChannel ();
				//synchronized (call)
				//{
				call.removeFromCalleeChannels (IDOfChannelToDrop);
				// It is OK to do this here, rather than wait to make sure that the channels were indeed removed, 
				// because as of this moment, we have "made" the new call. If there is any error here, the 
				// call has to drop, and we'll return CSMEvent.Error
				ActiveCallsHelper.updateActiveCallWithCalleeInfo (call.getUUID (), activeHandover.getCallerChannel (),
										  activeHandover.getCallerHandle ());
				call.addToCalleeChannels (stasisStart.getChannel ());
				//}
			    }
			else
			    {
				log.error ("The active handovers state for the call {} is incorrect! Could cause instability!", 
					   call.getUUID ());
				return CSMEvent.Error;
			    }

			log.debug ("The channel to drop is {}", IDOfChannelToDrop);
			removeChannelFromBridgeResponse = BridgesARIAPI.removeChannel (call.getBridge ().getId (), 
										       "?channel=" + IDOfChannelToDrop);

			if (removeChannelFromBridgeResponse.getStatus () == 400)
			    {
				log.error ("Channel {} not found while attempting to remove from bridge {}", 
					   IDOfChannelToDrop, call.getBridge ().getId ());
				return CSMEvent.Error;
			    }
			if (removeChannelFromBridgeResponse.getStatus () == 404)
			    {
				log.error ("Bridge {} not found", call.getBridge ().getId ());
				return CSMEvent.Error;
			    }
			if (removeChannelFromBridgeResponse.getStatus () == 409)
			    {
				log.error ("Bridge {} not in Stasis application", call.getBridge ().getId ());
				return CSMEvent.Error;
			    }
			if (removeChannelFromBridgeResponse.getStatus () == 422)
			    {
				log.error ("Channel {} not in this bridge {}", IDOfChannelToDrop, call.getBridge ().getId ());
				return CSMEvent.Error;
			    }
			/*
			 * You should also hang up this old channel
			 */
		
			ClientResponse channelHangupResponse = ChannelsARIAPI.hangup (IDOfChannelToDrop, "?reason=normal");
			if (channelHangupResponse.getStatus () == 400)
			    {
				log.error ("Invalid reason for hangup for channel {} provided", IDOfChannelToDrop);
				return CSMEvent.Error;
			    }
			if (channelHangupResponse.getStatus () == 404)
			    {
				log.error ("Channel {} not found", IDOfChannelToDrop);
				return CSMEvent.Error;
			    }
		
		
			log.debug ("Active handover completed. Details: {}", activeHandover.toJsonString ());
			/*
			  Next, delete the activeHandover
			*/
			ActiveHandoversHelper.removeActiveHandoverEntry (stasisStart.getChannel ().getId ());
			CallEventLogger.callLog (call, "Removing active handover information " + activeHandover.toJsonString ());
		    }
		else if (call.getLastCSMEvent () == CSMEvent.CalleeProcessing) // Processing for a new call; callee picked up! Yay!
		    {
			log.debug ("Before: " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()).toJsonString ());
			ActiveCallsHelper.updateActiveCallWithCalleeChannelID (call.getUUID (), 
									       stasisStart.getChannel ().getId ());

			log.debug ("After:  " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()).toJsonString ());
			//System.out.println ("Call between " + call.getCaller () + " and " + call.getCallee () + " established...");
			// PS: for this lastCSMEvent, the callee channel is added by 
			// ${ARI_APPLICATION_HOME}src/main/java/com/hola/serverSide/ariApplication/policy/*Action.java
		    }
		CallEventLogger.callLog (call, "Active call updated: [" + call.getLastCSMEvent () + "]: " +
					 ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()).toJsonString ());
	    }
	return CSMEvent.Success;
    }
}
