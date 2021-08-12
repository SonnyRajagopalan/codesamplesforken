package com.hola.serverSide.appInterface.policy;

import com.hola.serverSide.appInterface.call.ProtoCall;

public class ComparisonList
{
    public boolean isIn (ProtoCall _call)
    {
	// Error
	return false;
    }

    public boolean isNotIn (ProtoCall _call)
    {
	// Error
	return false;
    }

    public void print ()
    {
	// Overridden in the derived classes
    }
}
