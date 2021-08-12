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
 * Singleton for WaitForAddToActiveBridgeState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.StasisStart;
//import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
//import com.hola.serverSide.ariApplication.db.ActiveHandoversHelper;
import com.hola.serverSide.ariApplication.db.AccountHandlesHelper;
import com.hola.serverSide.ariApplication.db.HolaAccountsHelper;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;
//import com.hola.serverSide.ariApplication.db.beans.ActiveHandover;


public class CSMWaitForAddToActiveBridgeState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForAddToActiveBridgeState.class.getName ());
    // Eager initialization
    private static final CSMWaitForAddToActiveBridgeState instance = new CSMWaitForAddToActiveBridgeState  ();

    private CSMWaitForAddToActiveBridgeState ()
    {
	setState (CSMState.WaitForAddToActiveBridge);
    }
    
    public static CSMWaitForAddToActiveBridgeState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }
    
    // @Override
    // public void processSIPLegHandInForActiveCallRequested (Call call, Event event)
    // {
    // 	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
    //     process (call, event);	
    // }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		log.debug ("[Thread_ID: {}]: Successfully added new channelID to caller's channels for call {}/cState = {}", 
			   Thread.currentThread ().getId (), call.getUUID (), call.getCurrentState ().getStateName ());
		// 02.21.2016: Make before break mobility: we added the new channel to the bridge;
		//             now, we break much later, closer to moving to the ActiveCallState
		//call.changeState (getStateForStateName (CSMState.WaitForBreakOldLeg));
		//call.changeState (getStateForStateName (CSMState.WaitForAddOrRemoveSIPRegistrationListener));
		call.changeState (getStateForStateName (CSMState.WaitForConnectCallerAndCallee));
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
	StasisStart stasisStart = (StasisStart) event;
	ClientResponse addChannelResponse;
	String theOtherChannelIDToKeep="";
	String theChannelIDToRemove;
	String theNewChannelID = stasisStart.getChannel ().getId ();
	//String handoverRequestorType="TBD";
	String callerNumber = stasisStart.getChannel ().getCaller ().getNumber ();

	//Person thePersonWhoWantsToSwitchLegs = Person.getPersonFromHandle (stasisStart.getChannel ().getCaller ().getNumber ());
	HolaAccount thePersonWhoWantsToSwitchLegs = 
	    AccountHandlesHelper.getHolaAccountFromHandle (callerNumber);

	if (thePersonWhoWantsToSwitchLegs == null)
	    {
		// Likely because of the quirk that the handle for extensions don't exist in the handles part.
		// Let's get the hola account more directly, then.
		thePersonWhoWantsToSwitchLegs = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (callerNumber));
	    }

	// i.e., the caller is trying handout
	if (Integer.toString(thePersonWhoWantsToSwitchLegs.getExtension ()).equals (call.getCaller ()))
	    {
		theOtherChannelIDToKeep = call.getFirstCalleeChannelID ();
		call.addToCallerChannels (stasisStart.getChannel ());
		log.debug ("\n\n\n\n\n\n\nChecking caller: thePersonWhoWantsToSwitchLegs = " + 
			   thePersonWhoWantsToSwitchLegs.getExtension () +
			   ", call.getCaller () == " + call.getCaller () + ", stasisStart.getChannel ID == " + 
			   stasisStart.getChannel ().getCaller ().getNumber () + 
			   ", and the other leg to keep = " + theOtherChannelIDToKeep);
	    }
	else if (Integer.toString(thePersonWhoWantsToSwitchLegs.getExtension ()).equals (call.getCallee ()))
	    {
		theOtherChannelIDToKeep = call.getFirstCallerChannelID ();
		call.addToCalleeChannels (stasisStart.getChannel ());
		log.debug ("\n\n\n\n\n\n\nChecking callee: thePersonWhoWantsToSwitchLegs = " + 
				    thePersonWhoWantsToSwitchLegs.getExtension () +
				    ", call.getCallee () == " + call.getCallee () + ", stasisStart.getChannel ID == " + 
				    stasisStart.getChannel ().getCaller ().getNumber () + 
				    ", and the other leg to keep = " + theOtherChannelIDToKeep);

	    }
	//System.out.println ("handoverRequestorType " + handoverRequestorType);
	if (call.getBridge ().getId () != null)
	    {
		addChannelResponse = BridgesARIAPI.addChannel (call.getBridge ().getId (), 
							       "?channel=" + 
							       stasisStart.getChannel ().getId () + "," +
							       theOtherChannelIDToKeep);
		
		if (addChannelResponse.getStatus () == 400)
		    {
			log.error ("Channel {} supposedly in call {} was not found in Asterisk!", 
				   stasisStart.getChannel ().getId (), call.getUUID ());
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 404)
		    {
			log.error ("Bridge {} supposedly in call {} was not found in Asterisk!", 
				   call.getBridge ().getId (), call.getUUID ());
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 409)
		    {
			log.error ("Bridge {} not in Stasis application; channel currently recording!", 
				   call.getBridge ().getId ());
			return CSMEvent.Error;
		    }
		else if (addChannelResponse.getStatus () == 422)
		    {
			log.error ("Channel {} not in Stasis application!", 
				   stasisStart.getChannel ().getId ());
			return CSMEvent.Error;
		    }
	    }
	else
	    {
		log.error ("Cannot find bridge for call {}", call.getUUID ());
		return CSMEvent.Error;
	    }
	return CSMEvent.Success;
    }
}
