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
 * Singleton for WaitForDTMFState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;
import com.sun.jersey.api.client.ClientResponse;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola! specific
 */
import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CallDB;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.StasisEnd;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.beans.ActiveCall;

public class CSMWaitForDTMFState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForDTMFState.class.getName ());
    // Eager initialization
    private static final CSMWaitForDTMFState instance = new CSMWaitForDTMFState  ();

    private CSMWaitForDTMFState ()
    {
	setState (CSMState.WaitForDTMF);
    }

    public static CSMWaitForDTMFState getInstance ()
    {
	return instance;
    }

    @Override
    public void processFromPSTNForNewCall (Call call, Event event)
    {
	/*
	 * Answer this call first. Future events will be reaped by the processChannelDtmfReceived
	 * method
	 */
	log.debug (Thread.currentThread ().getStackTrace ().toString ());
	StasisStart stasisStart = (StasisStart) event;
	ClientResponse answerPSTNCallResponse = ChannelsARIAPI.answer (stasisStart.getChannel ().getId ());
		
	if (answerPSTNCallResponse.getStatus () == 404)
	    {
		log.error ("The channel {} was not found!", stasisStart.getChannel ().getId ());
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
	    }
	else if (answerPSTNCallResponse.getStatus () == 409)
	    {
		log.error ("The channel {} was not in stasis application!", stasisStart.getChannel ().getId ());
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
	    }

	// 05/08/2016: This feature seems to annoy a lot of people.
	// ClientResponse playMessageResponse = ChannelsARIAPI.play (stasisStart.getChannel ().getId (),
	// 							  "?media=sound:if-u-know-ext-dial");
	// if (playMessageResponse.getStatus () == 404)
	//     {
	// 	log.error ("Channel {} not found", stasisStart.getChannel ().getId ());
	// 	call.changeState (getStateForStateName (CSMState.Error));
	// 	call.processError (event);
	//     }
	// if (playMessageResponse.getStatus () == 409)
	//     {
	// 	log.error ("Channel {} not in a Stasis application", stasisStart.getChannel ().getId ());
	// 	call.changeState (getStateForStateName (CSMState.Error));
	// 	call.processError (event);
	//     }
    }
    
    @Override
    public void processIncomingHangup (Call call, Event event)
    {
	StasisEnd stasisEnd = (StasisEnd) event;
	call.changeState (getStateForStateName (CSMState.WaitForIncomingHangupProcessing));
	// Remove this call, as the other leg will not send a StasisEnd (there is _no_ other leg!)
	ActiveCallsHelper.removeUnfinishedCallByCallerChannel (stasisEnd.getChannel ().getId ());
	call.processIncomingHangup (event);
    }

    @Override
    public void processChannelDtmfReceived (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    public void process (Call call, Event event)
    {
	// TBD
	log.debug (Thread.currentThread ().getStackTrace ().toString ());
	switch (getTransitionForCallContext (call, event))
	    {
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
		break;
	    case DTMFRecognizedAsEmployee:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingMapping));
		call.processDTMFRecognizedAsEmployee (event);
		break;
	    case DTMFRecognizedAsExternalNumber:
		call.changeState (getStateForStateName (CSMState.WaitForExternalNumberValidation));
		call.processDTMFRecognizedAsExternalNumber (event);
		break;
	    case FromPSTNForNewCall:
		// Nothing to do; continue on.
	    default:
		// Stay in this state until the DTMF is "reaped"
	    }
    }

    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;
	String channelDTMFReceivedIn = channelDtmfReceived.getChannel ().getId ();
	String digitReceived = channelDtmfReceived.getDigit ();

	log.debug ("\n\n\n\n\t\t Thread ID " + Thread.currentThread ().getId () + 
			    " Digit received = " + digitReceived + ", total = " + 
			    call.getCallerDTMFReceived () + digitReceived + "\n\n\n\n\n");

	ActiveCall activeCall = ActiveCallsHelper.getActiveCallFromAChannelID (channelDTMFReceivedIn);

	//if (CallDB.channelIDIsCaller (channelDTMFReceivedIn))
	if (activeCall.getCallerChannel ().equals (channelDTMFReceivedIn))
	    {
		call.appendToCallerDTMFReceived (channelDtmfReceived.getDigit ());
		
		if (call.callerIsNotAnEmployee ())
		    {
			if (call.getCallerDTMFReceived ().substring (0, 1).equals ("6"))
			    {
				// trying to dial an extension
				// Max 4 digits
				if (call.getCallerDTMFReceived ().length () == 4)
				    {				
					/*
					 * This is the callee the external contact is attempting to reach.
					 * Move to the next state
					 */
					return CSMEvent.DTMFRecognizedAsEmployee;
				    }
				else
				    {
					return CSMEvent.FromPSTNForNewCall;
				    }
			    }
			else
			    {
				return call.handleIncorrectDTMF (event);
			    }

		    }
		else // In this case, caller is an employee
		    {
			if (call.getCallerDTMFReceived ().substring (0, 1).equals ("6"))
			    {
				// trying to dial an extension
				// Max 4 digits
				if (call.getCallerDTMFReceived ().length () == 4)
				    {				
					/*
					 * This is the callee the external contact is attempting to reach.
					 * Move to the next state
					 */
					return CSMEvent.DTMFRecognizedAsEmployee;
				    }
				else
				    {
					return CSMEvent.Success;
				    }
			    }
			else if (call.getCallerDTMFReceived ().substring (0, 1).equals ("9"))
			    {
				// trying to dial an external number
				if (call.getCallerDTMFReceived().length () == 2)
				    {
					if (!call.getCallerDTMFReceived ().equals ("91"))
					    {
						return call.handleIncorrectDTMF (event);
					    }
				    }

				if (call.getCallerDTMFReceived ().length () == 12) // 9-1-617-555-1212
				    {				
					/*
					 * This is the callee the external contact is attempting to reach.
					 * Move to the next state
					 */
					return CSMEvent.DTMFRecognizedAsExternalNumber;
				    }
				else
				    {
					return CSMEvent.FromPSTNForNewCall;
				    }
			    }
			else
			    {
				return call.handleIncorrectDTMF (event);
			    }
		    }
	    }
	//else if (CallDB.channelIDIsCallee (channelDTMFReceivedIn))
	else if (activeCall.getCalleeChannel ().equals (channelDTMFReceivedIn))
	    {
		log.debug ("For call {} in WaitForDTMF state, received DTMF from callee, when waiting for" + 
			   " DTMF to _determine_ the callee!", call.getUUID ());
		return CSMEvent.Error;
	    }
	else
	    {
		log.error ("Channel {} reaped by call {} is neither caller nor callee", 
			   channelDTMFReceivedIn, call.getUUID ());
		return CSMEvent.Error;
	    }
	
    }
}
