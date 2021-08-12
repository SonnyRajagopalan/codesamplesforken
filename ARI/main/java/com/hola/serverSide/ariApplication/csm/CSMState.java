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
 * Singleton for State state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

public enum CSMState
{
    Idle                                         (0),
    WaitForIncomingSetup                         (1),
    WaitForOutgoingChannelRequest                (2),
    WaitForOutgoingMapping                       (3),
    WaitForDTMF                                  (4),
    WaitForOutgoingChannelAnswer                 (5),
    WaitForMixingBridgeCreate                    (6),
    WaitForConnectCallerAndCallee                (7),
    WaitForUpdateActiveCallDB                    (8),
    ActiveCallState                              (9),
    WaitForExternalNumberValidation              (10),
    WaitForSIPChannelSetup                       (11),
    WaitForHandoutPrep                           (12),
    WaitForActiveCallDBLookup                    (13),
    WaitForAddToActiveBridge                     (14),
    //WaitForBreakOldLeg                           (15),
    WaitForAddOrRemoveSIPRegistrationListener    (16),
    WaitForIncomingHangupProcessing              (17),
    WaitForOutgoingHangupProcessing              (18), 
    Error                                        (97),
    CleanUp                                      (98),  // Cleanup the call state info, and exit the SM.
    UNKNOWN                                      (99);

    private final int stateInt;

    CSMState (int _stateInt)
    {
	this.stateInt = _stateInt;
    }

    public int getStateInt ()
    {
	return this.stateInt;
    }

    public CSMState getStateObjectForStateName (CSMState stateName)
    {
	return CSMState.UNKNOWN;
    }

    public String getStateString()
    {
	switch (this)
	    {
	    case Idle: 
		return "Idle";
		
	    case WaitForIncomingSetup: 
		return "WaitForIncomingSetup";
		
	    case WaitForOutgoingChannelRequest:
		return "WaitForOutgoingChannelRequest";

	    case WaitForOutgoingMapping: 
		return "WaitForOutgoingMapping";
		
	    case WaitForDTMF: 
		return "WaitForDTMF";
		
	    case WaitForOutgoingChannelAnswer: 
		return "WaitForOutgoingChannelAnswer";
		
	    case WaitForMixingBridgeCreate: 
		return "WaitForMixingBridgeCreate";

	    case WaitForConnectCallerAndCallee: 
		return "WaitForConnectCallerAndCallee";
		
	    case WaitForIncomingHangupProcessing: 
		return "WaitForIncomingHangupProcessing";
		
	    case WaitForOutgoingHangupProcessing: 
		return "WaitForOutgoingHangupProcessing";
		
	    case WaitForUpdateActiveCallDB: 
		return "WaitForUpdateActiveCallDB";
		
	    case ActiveCallState: 
		return "ActiveCallState";
		
	    case WaitForExternalNumberValidation: 
		return "WaitForExternalNumberValidation";
		
	    case WaitForSIPChannelSetup: 
		return "WaitForSIPChannelSetup";
		
	    case WaitForHandoutPrep: 
		return "WaitForHandoutPrep";
		
	    case WaitForActiveCallDBLookup: 
		return "WaitForActiveCallDBLookup";
		
	    case WaitForAddToActiveBridge: 
		return "WaitForAddToActiveBridge";
		
	    // case WaitForBreakOldLeg: 
	    // 	return "WaitForBreakOldLeg";
		
	    case WaitForAddOrRemoveSIPRegistrationListener: 
		return "WaitForAddOrRemoveSIPRegistrationListener";
		
	    case Error:
		return "Error";

	    case CleanUp:
		return "CleanUp";

	    default:
		return "UNKNOWN CSM STATE [" + this.stateInt + "]";
	    }
    }
}
