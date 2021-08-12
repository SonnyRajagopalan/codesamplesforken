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
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Condition
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (Condition.class.getName ());

    private final String type;

    public Condition (String _type)
    {
	this.type = _type;
    }

    public String getType ()
    {
	return this.type;
    }

    public boolean isValid (Call call)
    {
	// Error, should never be called
	return false;
    }

    public void print ()
    {
	// Should be overridden in derived classes
	log.error ("Eeek! Base class print () called!");
    }
}
