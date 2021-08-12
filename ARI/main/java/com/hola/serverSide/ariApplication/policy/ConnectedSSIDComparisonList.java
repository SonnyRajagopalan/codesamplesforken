/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

import java.util.List;

import com.hola.serverSide.ariApplication.call.Call;

public class ConnectedSSIDComparisonList extends ComparisonList
{
    private final List <String> SSIDs;

    ConnectedSSIDComparisonList (List <String> _SSIDs)
    {
	this.SSIDs = _SSIDs;
    }

    public boolean isInList (Call call)
    {
	boolean verity = false;
	// for (String SSID : SSIDs)
	//     {
	// 	if (SSID.equals (call.getCallerLocation ().getConnectedSSID ()))
	// 	    {
	// 		verity = true;
	// 	    }
	//     }
	return verity;
    }
}
