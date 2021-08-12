/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.people;

public enum HandleType
{
    VoIP              (1), // VoIP handle, e.g., PJSIP/Sonny.
    VoIPNumeric       (2), // VoIP extension number for easy dialpad calling. E.g., "6001"
    GoogleE164        (3), // Google voice number, a E.164 number from Google.
    Google            (4), // Google handle, like sonny.rajagopalan.
    SkypeE164         (5), // Skype phone number.
    Skype             (6), // Skype handle, like sonny.rajagopalan.
    LandlineE164      (7), // Landline E164 number.
    MobileE164        (8), // Mobile E164 number (from MNO).
    WhatsApp          (9), // WhatsApp account.
    FacebookMessenger (10), // Facebook Messenger account.


    UNKNOWN           (99); // Unknown handle.


    private final int handleTypeInt;


    HandleType (int _handleTypeInt)
    {
	this.handleTypeInt = _handleTypeInt;
    }

    public int getHandleTypeInt ()
    {
	return this.handleTypeInt;
    }

    public String getTypeString ()
    {
	switch (this)
	    {
	    case VoIP:
		{
		    return "VoIP";
		}
	    case VoIPNumeric:
		{
		    return "VoIPNumeric";
		}
	    case GoogleE164:
		{
		    return "GoogleE164";
		}
	    case Google:
		{
		    return "Google";
		}
	    case SkypeE164:
		{
		    return "SkypeE164";
		}
	    case Skype:
		{
		    return "Skype";
		}
	    case LandlineE164:
		{
		    return "LandlineE164";
		}
	    case MobileE164:
		{
		    return "MobileE164";
		}
	    case WhatsApp:
		{
		    return "WhatsApp";
		}
	    case FacebookMessenger:
		{
		    return "FacebookMessenger";
		}
	    case UNKNOWN:
		{
		    return "UNKNOWN";
		}
	    default:
		return "UNKNOWN HandleType TYPE";
	    }
    }
}
