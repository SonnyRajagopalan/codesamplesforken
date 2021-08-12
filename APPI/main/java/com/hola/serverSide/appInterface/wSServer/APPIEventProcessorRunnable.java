package com.hola.serverSide.appInterface.wSServer;

import com.hola.serverSide.appInterface.appi.APPIEventType;
import com.hola.serverSide.appInterface.appi.APPIEvent;

import java.lang.InterruptedException;

/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific imports
 */
import com.hola.serverSide.appInterface.appi.APPIEvent;
import com.hola.serverSide.appInterface.hsm.HSMIdleState;
import com.hola.serverSide.appInterface.peer.APPIPeerState;

final public class APPIEventProcessorRunnable implements Runnable
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (APPIEventProcessorRunnable.class.getName ());

    private APPIEvent appiEvent;
    private APPIPeerState peerState;
    private final long tID; // ID of the thread

    private long getCurrentThreadID ()
    {
	return this.tID;
    }

    public APPIEventProcessorRunnable (APPIPeerState _peerState, APPIEvent _appiEvent)
    {
	this.peerState = _peerState;
	this.appiEvent = _appiEvent;
	this.tID   = Thread.currentThread ().getId ();
    }

    @Override
    public void run ()
    {
	log.debug ("Kicking off a new thread {} for processing {} event for peer {} which is in {} state", 
		  Thread.currentThread ().getId (), this.appiEvent.getType (), this.peerState.getUUID (),
		  this.peerState.getCurrentState ().getStateName ());

	/*
	switch (this.appiEvent.getType ())
	    {
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
	*/
	HSMIdleState.getInstance ().processReceivedAPPIEvent (this.peerState, this.appiEvent);
    }
}
