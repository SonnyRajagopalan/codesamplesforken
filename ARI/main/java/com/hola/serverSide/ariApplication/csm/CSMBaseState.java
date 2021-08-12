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
 * Base state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.common.ThreadUtils;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMBaseState.class.getName ());

    private CSMState state;

    protected void setState (CSMState _state)
    {
	this.state = _state;
    }

    public void processIncomingHangup (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processOutgoingHangup (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processReceivedCallEvent (Event event) // Impossible to know call object before you've processed the event
    {
	// Error: code can never reach here
	log.error ("Error state: cannot reach here when processing received {} event", event.getType ());
    }

    public void processExtensionToExtensionCall (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processExtensionToExternalCall (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processFromPSTNForNewCall (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processFromPSTNForExistingCall (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processSIPLegHandInForActiveCallRequested (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processCalleeProcessing (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processSIPEndpointOfActiveCallRegistered (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processError (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processCleanUp (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }


    public void processOutgoingMappingNotFound (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processSuccess (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processCallSetupFailureIncomingChannelHungup (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processCallSetupFailureOutgoingChannelHungup (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processOutgoingChannelNotAvailable (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processOutgoingChannelAvailable (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processOutgoingMappingFound (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processOutgoingChannelRing (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processChannelDtmfReceived (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processDTMFRecognizedAsEmployee (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public void processDTMFRecognizedAsExternalNumber (Call call, Event event)
    {
	// Error: code can never reach here
	log.error ("[Thread_ID: {}] Call {} is in error state (current state is {}, event received is {})",
		   ThreadUtils.getThreadIDString (), call.getUUID (), call.getCurrentStateName (), event.getType ());
    }

    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	return CSMEvent.UNKNOWN;
    }


    public String getStateName ()
    {
	return this.state.getStateString ();
    }

    public void changeState (Call call, CSMBaseState newState)
    {
	log.debug ("[Thread_ID: {}] Call {}: state change from {} to {}", call.getUUID (), 
		   call.getCurrentStateName (), newState.getStateName ());
	call.changeState (newState);
    }

    public static synchronized CSMBaseState getStateForStateName (CSMState state)
    {
	switch (state)
	    {
	    case Idle:
		{
		    return CSMIdleState.getInstance ();
		}
	    case Error:
		{
		    return CSMErrorState.getInstance ();
		}
	    case CleanUp:
		{
		    return CSMCleanUpState.getInstance ();
		}
	    case WaitForIncomingSetup:
		{
		    return CSMWaitForIncomingSetupState.getInstance ();
		}
	    case WaitForOutgoingChannelRequest:
		{
		    return CSMWaitForOutgoingChannelRequestState.getInstance ();
		}
	    case WaitForOutgoingMapping:
		{
		    return CSMWaitForOutgoingMappingState.getInstance ();
		}
	    case WaitForDTMF:
		{
		    return CSMWaitForDTMFState.getInstance ();
		}
	    case WaitForOutgoingChannelAnswer:
		{
		    return CSMWaitForOutgoingChannelAnswerState.getInstance ();
		}
	    case WaitForMixingBridgeCreate:
		{
		    return CSMWaitForMixingBridgeCreateState.getInstance ();
		}
	    case WaitForIncomingHangupProcessing:
		{
		    return CSMWaitForIncomingHangupProcessingState.getInstance ();
		}
	    case WaitForOutgoingHangupProcessing:
		{
		    return CSMWaitForOutgoingHangupProcessingState.getInstance ();
		}
	    case WaitForConnectCallerAndCallee:
		{
		    return CSMWaitForConnectCallerAndCalleeState.getInstance ();
		}
	    case WaitForUpdateActiveCallDB:
		{
		    return CSMWaitForUpdateActiveCallDBState.getInstance ();
		}
	    case ActiveCallState:
		{
		    return CSMActiveCallState.getInstance ();
		}
	    case WaitForExternalNumberValidation:
		{
		    return CSMWaitForExternalNumberValidationState.getInstance ();
		}
	    case WaitForSIPChannelSetup:
		{
		    return CSMWaitForSIPChannelSetupState.getInstance ();
		}
	    case WaitForHandoutPrep:
		{
		    return CSMWaitForHandoutPrepState.getInstance ();
		}
	    case WaitForAddToActiveBridge:
		{
		    return CSMWaitForAddToActiveBridgeState.getInstance ();
		}
	    // case WaitForBreakOldLeg:
	    // 	{
	    // 	    return CSMWaitForBreakOldLegState.getInstance ();
	    // 	}
	    case WaitForAddOrRemoveSIPRegistrationListener:
		{
		    return CSMWaitForAddOrRemoveSIPRegistrationListenerState.getInstance ();
		}
	    default:
		return null;
	    }
    }
}
