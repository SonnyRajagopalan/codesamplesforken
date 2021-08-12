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
 * Singleton for ActiveCallState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.common.Color;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMActiveCallState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMActiveCallState.class.getName ());
    // Eager initialization
    private static final CSMActiveCallState instance = new CSMActiveCallState  ();

    private CSMActiveCallState ()
    {
	setState (CSMState.ActiveCallState);
    }
    
    public static CSMActiveCallState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {} is in active call state", call.getUUID (), event.getType ());
	call.logObject ();
        System.out.println (Color.BrightGreen + "Call@" + Integer.toHexString(System.identityHashCode(call)) + Color.End + 
			    Color.Red + " (WARNING! These call hashIDs are NOT unique, only call UUIDs are unique.) " + Color.End + Color.BrightGreen + 
			    " in ActiveCallState: " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()).toJsonString () + Color.End);
	//System.out.println (Color.BrightGreen + "Call in ActiveCallState: " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()).toJsonString () + Color.End);
	return;
    }

    @Override
    public void processSIPLegHandInForActiveCallRequested (Call call, Event event)
    {
	//call.changeState (getStateForStateName (CSMState.WaitForAddToActiveBridge));
	call.changeState (getStateForStateName (CSMState.WaitForHandoutPrep));
	call.processSIPLegHandInForActiveCallRequested (event);
    }

    @Override
    public void processFromPSTNForExistingCall (Call call, Event event)
    {
	call.changeState (getStateForStateName (CSMState.WaitForHandoutPrep));
	call.processFromPSTNForExistingCall (event);
    }

    @Override
    public void processIncomingHangup (Call call, Event event)
    {
	//System.out.println ("processing incoming hangup...");
	call.changeState (getStateForStateName (CSMState.WaitForIncomingHangupProcessing));
	call.processIncomingHangup (event);
    }

    @Override
    public void processOutgoingHangup (Call call, Event event)
    {
	//System.out.println ("processing outgoing hangup...");
	call.changeState (getStateForStateName (CSMState.WaitForOutgoingHangupProcessing));
	call.processOutgoingHangup (event);
    }
}
