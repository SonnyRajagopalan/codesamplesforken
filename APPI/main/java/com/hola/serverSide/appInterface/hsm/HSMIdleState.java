/*
 * 12.29.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The HAPPI State Machine: Idle
 *
 * Singleton for Idle state of the HAPPI State Machine
 */
package com.hola.serverSide.appInterface.hsm;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific imports
 */
import com.hola.serverSide.appInterface.peer.APPIPeerState;
import com.hola.serverSide.appInterface.appi.APPIEvent;
import com.hola.serverSide.appInterface.appi.APPIEventType;
import com.hola.serverSide.appInterface.appi.AuthRequest;
import com.hola.serverSide.appInterface.appi.StatusUpdate;

public class HSMIdleState extends HSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HSMIdleState.class.getName ());
    // Eager initialization
    private static final HSMIdleState instance = new HSMIdleState  ();

    private HSMIdleState ()
    {
	super (HSMState.Idle);
	//setState (HSMState.Idle);
    }
    
    public static HSMIdleState getInstance ()
    {
	return instance;
    }

    @Override
    public void processReceivedAPPIEvent (APPIPeerState peerState, APPIEvent appiEvent)
    {
	/*
	 * This is where we will establish a new peer state or connect an APPI event to an
	 * existing peer state.
	 */

	//APPIPeerState peerState = null;
	HSMEvent transition = HSMEvent.UNKNOWN;
	/*
	  First, establish the event context, i.e., the peer that we are processing this event for
	*/
	log.debug (appiEvent.toJsonString ());
	switch (appiEvent.getType ())
	    {
	    case "AuthRequest":
		{
		    log.debug ("{} Processing an AuthRequest for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.processReceivedAuthRequest (appiEvent);
		    break;
		}
	    case "InfoRequest":
		{
		    log.debug ("{} Processing an InfoRequest for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedInfoRequest);
		    peerState.processReceivedInfoRequest (appiEvent);
		    break;
		}
	    case "ContactAddRequest":
		{
		    log.debug ("{} Processing a ContactAddRequest for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedContactAddRequest);
		    peerState.processReceivedContactAddRequest (appiEvent);
		    break;
		}
	    case "StatusUpdate":
		{
		    log.debug ("{} Processing a StatusUpdate for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());

		    peerState.setLastHSMEvent (HSMEvent.ReceivedStatusUpdate);
		    peerState.processReceivedStatusUpdate (appiEvent);
		    break;
		}
	    case "ReachabilityRequest":
		{
		    log.debug ("{} Processing a ReachabilityRequest for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedReachabilityRequest);
		    peerState.processReceivedReachabilityRequest (appiEvent);
		    break;
		}
	    case "GoingToBackground":
		{
		    log.debug ("{} Processing a GoingToBackground message for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedGoingToBackground);
		    peerState.processReceivedGoingToBackground (appiEvent);
		    break;		    
		}
	    case "GoingToForeground":
		{
		    log.debug ("{} Processing a GoingToForeground message for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedGoingToForeground);
		    peerState.processReceivedGoingToForeground (appiEvent);
		    break;		    
		}
	    case "CurrentCallRequest":
		{
		    log.debug ("{} Processing a CurrentCallRequest for peer UUID {} which is in state {}", 
			      Thread.currentThread ().getId (),
			      peerState.getUUID (), peerState.getCurrentState ().getStateName ());
		    peerState.setLastHSMEvent (HSMEvent.ReceivedCurrentCallRequest);
		    peerState.processReceivedCurrentCallRequest (appiEvent);
		    break;
		}
	    default:
		log.error ("Unknown (or uncaught?) appiEvent type for peer {}", peerState.getUUID ());
	    }

	if (peerState != null)
	    {
		//makeCorrectTransitions (peerState, appiEvent);
	    }
	else
	    {
		log.error ("{} Not able to pin-point the peer this {} event is intended to", 
			   Thread.currentThread ().getId (), appiEvent.getType ());
	    }
    }
}
