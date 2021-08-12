package com.hola.serverSide.appInterface.policy;

import java.util.List;

import com.hola.serverSide.appInterface.call.ProtoCall;

public class ConnectedSSIDComparisonList extends ComparisonList
{
    private final List <String> SSIDs;

    ConnectedSSIDComparisonList (List <String> _SSIDs)
    {
	this.SSIDs = _SSIDs;
    }

    public boolean isInList (ProtoCall call)
    {
	boolean verity = false;
	for (String SSID : SSIDs)
	    {
		if (SSID.equals (call.getCallerInfo ().getLocation ().getConnectedSSID ()))
		    {
			verity = true;
		    }
	    }
	return verity;
    }
}
