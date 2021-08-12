/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

import com.hola.serverSide.ariApplication.call.Call;

public class ComparisonList
{
    public boolean isIn (Call _call)
    {
	// Error
	return false;
    }

    public boolean isNotIn (Call _call)
    {
	// Error
	return false;
    }

    public void print ()
    {
	// Overridden in the derived classes
    }
}
