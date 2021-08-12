/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.charging;

public enum ServiceType
{
    PSTNVoice                 (1),
    HolaVoice                 (2),
    GoogleVoice               (3),
    SkypeVoice                (4),
    HolaVideo                 (5),
    GoogleVideo               (6),
    SkypeVideo                (7),
    FaceTimeVideo             (8),
    PSTNSMS                   (9),
    SkypeMessage              (10),
    GoogleMessage             (11),
    WhatsAppMessage           (12),
    FacebookMessengerMessage  (13);

    private final int typeInt;

    ServiceType (int _typeInt)
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
	    case PSTNVoice:
		{
		    return "PSTNVoice";
		}
	    case HolaVoice:
		{
		    return "HolaVoice";
		}
	    case GoogleVoice:
		{
		    return "GoogleVoice";
		}
	    case SkypeVoice:
		{
		    return "SkypeVoice";
		}
	    case HolaVideo:
		{
		    return "HolaVideo";
		}
	    case GoogleVideo:
		{
		    return "GoogleVideo";
		}
	    case SkypeVideo:
		{
		    return "SkypeVideo";
		}
	    case FaceTimeVideo:
		{
		    return "FaceTimeVideo";
		}
	    case PSTNSMS:
		{
		    return "PSTNSMS";
		}
	    case SkypeMessage:
		{
		    return "SkypeMessage";
		}
	    case GoogleMessage:
		{
		    return "GoogleMessage";
		}
	    case WhatsAppMessage:
		{
		    return "WhatsAppMessage";
		}
	    case FacebookMessengerMessage:
		{
		    return "FacebookMessengerMessage";
		}
	    default:
		return "UNKNOWN ServiceType TYPE";
	    }
    }
}
