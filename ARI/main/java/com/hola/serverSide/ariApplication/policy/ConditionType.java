/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

public enum ConditionType
{
    Time          (1),
    User          (2),
    SSID          (3),
    ThreeGPP_MCC  (4),
    ThreeGPP_MNC  (5),
    Default       (98),
    UNKNOWN       (99);

    private final int typeInt;


    ConditionType (int _typeInt)
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
	    case Time:
		{
		    return "Time";
		}
	    case User:
		{
		    return "User";
		}
	    case SSID:
		{
		    return "SSID";
		}
	    case ThreeGPP_MCC:
		{
		    return "ThreeGPP_MCC";
		}
	    case ThreeGPP_MNC:
		{
		    return "ThreeGPP_MNC";
		}
	    case Default:
		{
		    return "Default";
		}
	    case UNKNOWN:
		{
		    return "UNKNOWN";
		}
	    default:
		return "UNKNOWN ConditionType TYPE";
	    }
    }
}
