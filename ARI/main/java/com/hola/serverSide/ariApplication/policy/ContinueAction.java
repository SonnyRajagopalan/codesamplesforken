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
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CalleeUtils;
import com.hola.serverSide.ariApplication.common.Result;

public class ContinueAction extends Action
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (ContinueAction.class.getName ());

    public ContinueAction ()
    {
	super (ActionType.Continue);
    }

    @Override
    public Result execute (Call call, String...options)
    {
	if (options [0].equals (Policy.EXECUTE_POLICY))
	    {
		log.debug ("In ContinueAction::Execute ()");
		String query = CalleeUtils.getQueryStringToPlaceCall (call);
		log.debug ("Will create callee channel with " + query);
		Result result = CalleeUtils.originateCalleeChannelWithQueryStringAndBody (call, query, "");
		
		return result;
	    }
	else
	    {
		log.debug ("In ContinueAction::Execute () /dry run");
		return new Result ();
	    }
    }

    @Override
    public void print ()
    {
	log.debug ("\t\tContinue");
    }
}
