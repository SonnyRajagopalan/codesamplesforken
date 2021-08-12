/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

public enum ActionType
{
    Log                      (1),
    Continue                 (2),
    Reject                   (3),
    VoiceMail                (4),
    RedirectToSkype          (5),
    RedirectToGoogleVoice    (6),
    RedirectToLandlinePSTN   (7),
    RedirectToMobilePSTN     (8),
    SendSMSToMobile          (9),
    SendTextToVoIP           (11),
    FacebookMessengerMessage (12),
    WhatsAppMessage          (13),
    UNKNOWN                  (99);

    private final int typeInt;

    ActionType (int _typeInt)
    {
	this.typeInt = _typeInt;
    }

    public int getTypeInt ()
    {
	return typeInt;
    }

    public String getTypeString ()
    {
	switch (this)
	    {
	    case Log:
		{
		    return "Log";
		}
	    case Continue:
		{
		    return "Continue";
		}
	    case Reject:
		{
		    return "Reject";
		}
	    case VoiceMail:
		{
		    return "VoiceMail";
		}
	    case RedirectToSkype:
		{
		    return "RedirectToSkype";
		}
	    case RedirectToGoogleVoice:
		{
		    return "RedirectToGoogleVoice";
		}
	    case RedirectToLandlinePSTN:
		{
		    return "RedirectToLandlinePSTN";
		}
	    case RedirectToMobilePSTN:
		{
		    return "RedirectToMobilePSTN";
		}
	    case SendSMSToMobile:
		{
		    return "SendSMSToMobile";
		}
	    case SendTextToVoIP:
		{
		    return "SendTextToVoIP";
		}
	    case FacebookMessengerMessage:
		{
		    return "FacebookMessengerMessage";
		}
	    case WhatsAppMessage:
		{
		    return "WhatsAppMessage";
		}
	    case UNKNOWN:
		{
		    return "UNKNOWN";
		}
	    default:
		return "UNKNOWN ActionType TYPE";
	    }
    }


    public static Action getActionFromTypeString (String actionTypeString)
    {
	switch (actionTypeString)
	    {
	    case "Log":
		{
		    return new LogAction ();
		}
	    case "Continue":
		{
		    return new ContinueAction ();
		}
	    case "Reject":
		{
		    return new RejectAction ();
		}
	    case "VoiceMail":
		{
		    return new VoiceMailAction ();
		}
	    case "RedirectToSkype":
		{
		    return new RedirectToSkypeAction ();
		}
	    case "RedirectToGoogleVoice":
		{
		    return new RedirectToGoogleVoiceAction ();
		}
	    case "RedirectToLandlinePSTN":
		{
		    return new RedirectToLandlinePSTNAction ();
		}
	    case "RedirectToMobilePSTN":
		{
		    return new RedirectToMobilePSTNAction ();
		}
	    case "SendSMSToMobile":
		{
		    return new SendSMSToMobileAction ();
		}
	    case "SendTextToVoIP":
		{
		    return new SendTextToVoIPAction ();
		}

	    case "FacebookMessengerMessage":
		{
		    return new FacebookMessengerMessageAction ();
		}
	    case "WhatsAppMessage":
		{
		    return new WhatsAppMessageAction ();
		}
	    case "UNKNOWN":
		{
		    //return new UNKNOWNAction ();
		}
	    default:
		{
		}
		return new Action (ActionType.UNKNOWN);
	    }
    }

}
