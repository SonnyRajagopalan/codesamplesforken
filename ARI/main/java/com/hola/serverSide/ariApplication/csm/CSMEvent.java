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
 * Singleton for Event state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

public enum CSMEvent
{
    /*
      Single entry
    */

    ReceiveCallEvent                      (0),
    /*
      Call types
    */    
    ExtensionToExtensionCall              (1),
    ExtensionToExternalCall               (2),
    FromPSTNForNewCall                    (3),
    FromPSTNForExistingCall               (4),
    CalleeProcessing                      (5),
    SIPEndpointOfActiveCallRegistered     (6),
    SIPLegHandInForActiveCallRequested    (7),

    /*
      Errors
    */
    Error                                 (101),

    /*
      Good transitions
    */
    Success                               (200),
    
    /*
      Regular transitions
    */
    CallSetupFailureIncomingChannelHungup (301),
    CallSetupFailureOutgoingChannelHungup (302),
    OutgoingChannelNotAvailable           (303),
    OutgoingChannelAvailable              (304),
    OutgoingMappingFound                  (305),
    OutgoingMappingNotFound               (306),
    OutgoingChannelRing                   (307),
    OutgoingChannelHungup                 (308),
    IncomingChannelHungup                 (309),
    DTMFRecognizedAsEmployee              (310),
    DTMFRecognizedAsExternalNumber        (311),

    /*
     * Policy related transitions
     */
    Continue                              (401),
    SendText                              (402),
    SendSMS                               (403),
    SendEmail                             (404),
    SendIPMessage                         (405),
    RedirectToVoiceMail                   (406),
    RedirectToLandlinePSTN                (407),
    RedirectToMobilePSTN                  (408),
    RedirectToSkype                       (409),
    RedirectToGoogleVoice                 (410),
    RedirectToWhatsApp                    (411),
    RedirectToFacebookMessenger           (412),
    Reject                                (490),

    CleanUp                               (501),
    /*
     * Transitions that help pend
     * execution on the thread
     */
    WaitForExternalEvent                  (600),
    /*
     * Error transitions
     */
    UNKNOWN                               (999);
    
    private int callEventTypeInt;

    CSMEvent (int _callEventTypeInt)
    {
	this.callEventTypeInt = _callEventTypeInt;
    }

    public int getCallEventTypeInt ()
    {
	return this.callEventTypeInt;
    }

    public String getCallEventTypeString ()
    {
	switch (this)
	    {
	    case ExtensionToExtensionCall:
		return "ExtensionToExtensionCall";
		
	    case ExtensionToExternalCall:
		return "ExtensionToExternalCall";
		
	    case FromPSTNForNewCall:
		return "FromPSTNForNewCall";
		
	    case FromPSTNForExistingCall:
		return "FromPSTNForExistingCall";
		
	    case CalleeProcessing:
		return "CalleeProcessing";
		
	    case SIPEndpointOfActiveCallRegistered:
		return "SIPEndpointOfActiveCallRegistered";

	    case SIPLegHandInForActiveCallRequested:
		return "SIPLegHandInForActiveCallRequested";
		
	    case Error:
		return "Error";
		
	    case CleanUp:
		return "CleanUp";

	    case OutgoingMappingNotFound:
		return "OutgoingMappingNotFound";
		
	    case CallSetupFailureIncomingChannelHungup:
		return "CallSetupFailureIncomingChannelHungup";
		
	    case CallSetupFailureOutgoingChannelHungup:
		return "CallSetupFailureOutgoingChannelHungup";
		
	    case OutgoingChannelNotAvailable:
		return "OutgoingChannelNotAvailable";
		
	    case OutgoingChannelAvailable:
		return "OutgoingChannelAvailable";
		
	    case OutgoingMappingFound:
		return "OutgoingMappingFound";
		
	    case OutgoingChannelRing:
		return "OutgoingChannelRing";

	    case DTMFRecognizedAsEmployee:
		return "DTMFRecognizedAsEmployee";
			      
	    case DTMFRecognizedAsExternalNumber:
		return "DTMFRecognizedAsExternalNumber";

	    case Continue:
		return "Continue";

	    case SendText:
		return "SendText";

	    case SendSMS:
		return "SendSMS";

	    case SendEmail:
		return "SendEmail";

	    case SendIPMessage:
		return "SendIPMessage";

	    case RedirectToVoiceMail:
		return "RedirectToVoiceMail";

	    case RedirectToLandlinePSTN:
		return "RedirectToLandlinePSTN";

	    case RedirectToMobilePSTN:
		return "RedirectToMobilePSTN";

	    case RedirectToSkype:
		return "RedirectToSkype";

	    case RedirectToGoogleVoice:
		return "RedirectToGoogleVoice";

	    case RedirectToWhatsApp:
		return "RedirectToWhatsApp";

	    case RedirectToFacebookMessenger:
		return "RedirectToFacebookMessenger";

	    case Reject:
		return "Reject";

	    case IncomingChannelHungup:
		return "IncomingChannelHungup";

	    case OutgoingChannelHungup:
		return "OutgoingChannelHungup";

	    default:
		return "UNKNOWN CSM EVENT [" + this.callEventTypeInt + "]";
	    }
    }
}
