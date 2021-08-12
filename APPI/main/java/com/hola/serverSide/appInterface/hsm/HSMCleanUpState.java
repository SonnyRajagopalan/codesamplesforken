/*
 * 12.29.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The HAPPI State Machine: CleanUp
 *
 * Singleton for CleanUp state of the HAPPI State Machine
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

public class HSMCleanUpState extends HSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HSMCleanUpState.class.getName ());
    // Eager initialization
    private static final HSMCleanUpState instance = new HSMCleanUpState  ();

    private HSMCleanUpState ()
    {
	super (HSMState.CleanUp);
    }
    
    public static HSMCleanUpState getInstance ()
    {
	return instance;
    }

    public void process (APPIPeerState peerState, APPIEvent event)
    {
    }

    @Override
    public HSMEvent getTransitionForPeerContext (APPIPeerState peerState, APPIEvent event)
    {
	return HSMEvent.Error;
    }
}
