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

public class LogAction extends Action
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (LogAction.class.getName ());

    public LogAction ()
    {
	super (ActionType.Log);
    }

    @Override
    public Result execute (Call call, String...options)
    {
	log.debug ("Log action: {}", call.getUUID ());
	return new Result ();
    }

    @Override
    public void print ()
    {
	log.debug ("\t\tLog");
    }
}
