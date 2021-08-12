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
 * Singleton for WaitForOutgoingMappingState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.db.HolaAccountsHelper;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;

import com.sun.jersey.api.client.ClientResponse;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForOutgoingMappingState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForOutgoingMappingState.class.getName ());
    // Eager initialization
    private static final CSMWaitForOutgoingMappingState instance = new CSMWaitForOutgoingMappingState  ();

    private CSMWaitForOutgoingMappingState ()
    {
	setState (CSMState.WaitForOutgoingMapping);
    }

    public static CSMWaitForOutgoingMappingState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }


    @Override
    public void processDTMFRecognizedAsEmployee (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processOutgoingChannelNotAvailable (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	// TBD
	switch (getTransitionForCallContext (call, event))
	    {
	    // case OutgoingMappingNotFound:
	    // 	call.changeState (getStateForStateName (CSMState.WaitForIncomingHangupProcessingForError));
	    // 	call.processOutgoingMappingNotFound (event);
	    // 	break;
	    case OutgoingMappingFound:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelRequest));
		call.processOutgoingMappingFound (event);
		break;
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
		break;
	    case CleanUp:
		call.changeState (getStateForStateName (CSMState.CleanUp));
		call.processSuccess (event);
		break;
	    default:
		// Error
	    }
    }

    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	// Called only from WaitForDTMFState--check if the entered DTMF maps to an employee
	ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;

	log.debug ("\n\n\n\n\t\t Thread ID " + Thread.currentThread ().getId () + 
			    ": WaitForOutgoingMapping--processDTMFRecognizedAsEmployee: Entered DTMF = " + 
			    call.getCallerDTMFReceived ());
	String DTMFReceivedFromCaller = call.getCallerDTMFReceived ();
	//Person employeeBeingTried = Person.getPersonFromHandle (DTMFReceivedFromCaller);
	HolaAccount employeeBeingTried = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (DTMFReceivedFromCaller));
	
	//call.resetCallerDTMFReceivedSoFar ();

	if (employeeBeingTried == null)
	    {
		log.debug ("No employee found at extension {}. Returning flow to recover DTMF after message", 
			   DTMFReceivedFromCaller);

		return call.handleIncorrectDTMF (event);
	    }
	else
	    {
		// 05/08/2016: This feature seems to annoy a lot of people.
		// ClientResponse playMessageResponse = ChannelsARIAPI.play (channelDtmfReceived.getChannel ().getId (),
		// //ClientResponse playMessageResponse = ChannelsARIAPI.play ("NON_Existent_Channel", // testing the codeflow
		// 							  "?media=sound:good");
		// if (playMessageResponse.getStatus () == 404)
		//     {
		// 	log.error ("Channel {} not found", channelDtmfReceived.getChannel ().getId ());
		// 	//return CSMEvent.Error; HARIB hardening: 02-25-2016: Not an error because the caller may have hung up!
		// 	//                       But, please also remove the ActiveCall asssociated with this call
		// 	ActiveCallsHelper.removeUnfinishedCallByCallerChannel (channelDtmfReceived.getChannel ().getId ());
		// 	call.removeFromCallerChannels (channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.CleanUp;
		//     }
		// if (playMessageResponse.getStatus () == 409)
		//     {
		// 	log.error ("Channel {} not in a Stasis application", channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.Error;
		//     }

		return CSMEvent.OutgoingMappingFound;
	    }
	
    }
}
