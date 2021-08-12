/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  Hola
 */
import com.hola.serverSide.ariApplication.call.Call;

import com.hola.serverSide.ariApplication.common.Result;

public class Action
{
 // Logging
    private static final Logger log = LoggerFactory.getLogger (Action.class.getName ());

    private final ActionType actionType;

    public Action (ActionType _actionType)
    {
	this.actionType = _actionType;
    }

    public Result execute (Call call, String...options)
    {
	Result result = null;
	// Error log here
	log.error ("execute () called in base state. Error");
	return result;
    }

    public String getType ()
    {
	return actionType.getTypeString ();
    }

    public void print ()
    {
	// Should be overridden in derived classes
	log.error ("print () called in base state. Error");
    }
}
