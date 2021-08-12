/*
 * 12.29.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! HAPPI State Machine
 *
 * Base state of the HAPPI State Machine
 */
package com.hola.serverSide.appInterface.hsm;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific
 */

import com.hola.serverSide.appInterface.peer.APPIPeerState;
import com.hola.serverSide.appInterface.appi.APPIEvent;

public class HSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HSMBaseState.class.getName ());

    protected HSMState state;

    // protected HSMBaseState ()
    // {
    // }

    protected HSMBaseState (HSMState _state)
    {
    	this.state = _state;
    }

    protected void setState (HSMState _state)
    {
	this.state = _state;
    }

    public void processReceivedAPPIEvent (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Event processing attempted in base state for event {}", event.getType ());
    }

    public void processReceivedAuthRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: event processing attempted in base state for event {}", peerState.getUUID (),
		   event.getType ());
    }

    public void processReceivedContactAddRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: event processing attempted in base state for event {}", peerState.getUUID (),
		   event.getType ());
    }

    public void processAndSendFailedAuthResponse (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processAndSendFailedAuthResponse () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }    

    public void processAndSendSuccessAuthResponse (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processAndSendSuccessAuthResponse () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processFailedAuthRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processFailedAuthRequest () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedInfoRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processReceivedInfoRequest () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processSendReachabilityUpdate (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processSendReachabilityUpdate () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedStatusUpdate (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processReceivedStatusUpdate () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedReachabilityRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processReceivedReachabilityRequest () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedGoingToBackground (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processReceivedGoingToBackground () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedGoingToForeground (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processReceivedGoingToForeground () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedCurrentCallRequest (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: event processing attempted in base state for event {}/" + 
"current state {}", peerState.getUUID (),
		   event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processSuccess (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processSuccess () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processCleanUp (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processCleanUp () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }

    public void processError (APPIPeerState peerState, APPIEvent event)
    {
	log.error ("Peer {} is in error state: processError () attempted in base state for event {}/" + 
"current state {}", 
		   peerState.getUUID (), event.getType (), peerState.getCurrentState ().getStateName ());
    }


    public HSMEvent getTransitionForPeerContext (APPIPeerState peerState, APPIEvent event)
    {
	return HSMEvent.UNKNOWN;
    }


    public String getStateName ()
    {
	return this.state.getStateString ();
    }

    public void changeState (APPIPeerState peerState, HSMBaseState newState)
    {
	peerState.changeState (newState);
    }

    public static synchronized HSMBaseState getStateForStateName (HSMState state)
    {
	switch (state)
	    {
	    case Idle:
		{
		    return HSMIdleState.getInstance ();
		}

	    // case WaitForAuth:
	    // 	{
	    // 	    return HSMWaitForAuthState.getInstance ();
	    // 	}

	    case ActiveHAPPI:
		{
		    return HSMActiveHAPPIState.getInstance ();
		}

	    // case WaitForInfoRequestProcessing:
	    // 	{
	    // 	    return HSMWaitForInfoRequestProcessingState.getInstance ();
	    // 	}
	    // case WaitForReachabilityUpdateResponse:
	    // 	{
	    // 	    return HSMWaitForReachabilityUpdateResponseState.getInstance ();
	    // 	}

	    // case WaitForReachabilityRequestProcessing:
	    // 	{
	    // 	    return HSMWaitForReachabilityRequestProcessingState.getInstance ();
	    // 	}

	    // case WaitForStatusUpdateProcessing:
	    // 	{
	    // 	    return HSMWaitForStatusUpdateProcessingState.getInstance ();
	    // 	}
	    // case WaitForContactAddRequestProcessing:
	    // 	{
	    // 	    return HSMWaitForContactAddRequestProcessingState.getInstance ();
	    // 	}
	    // case WaitForCurrentCallRequestProcessing:
	    // 	{
	    // 	    return HSMWaitForCurrentCallRequestProcessingState.getInstance ();
	    // 	}
	    case WaitForWakeUp:
		{
		    return HSMWaitForWakeUpState.getInstance ();
		}
	    case CleanUp:
		{
		    return HSMCleanUpState.getInstance ();
		}

	    case Error:
		{
		    return HSMErrorState.getInstance ();
		}
	    default:
		return null;
	    }
    }
}
