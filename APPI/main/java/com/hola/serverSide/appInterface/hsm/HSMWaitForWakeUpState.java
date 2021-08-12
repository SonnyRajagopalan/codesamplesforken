/*
 * 12.29.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The HAPPI State Machine: WaitForWakeUp
 *
 * Singleton for WaitForWakeUp state of the HAPPI State Machine
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

public class HSMWaitForWakeUpState extends HSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HSMWaitForWakeUpState.class.getName ());
    // Eager initialization
    private static final HSMWaitForWakeUpState instance = new HSMWaitForWakeUpState  ();

    private HSMWaitForWakeUpState ()
    {
	super (HSMState.WaitForWakeUp);
	//setState (HSMState.WaitForWakeUp);
    }
    
    public static HSMWaitForWakeUpState getInstance ()
    {
	return instance;
    }

    public void processReceivedGoingToForeground (APPIPeerState peerState, APPIEvent event)
    {
	process (peerState, event);
    }

    public void process (APPIPeerState peerState, APPIEvent event)
    {
	log.debug ("Woke up owing to GoingToForeground received for peerState whose currentState = {}", 
		  peerState.getCurrentState ().getStateName ());
	peerState.changeState (getStateForStateName (HSMState.ActiveHAPPI));
	// Just park there for the next event
    }

}
