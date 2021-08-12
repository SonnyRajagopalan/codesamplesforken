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
import com.hola.serverSide.ariApplication.common.Result;

public class RedirectToMobilePSTNAction extends Action
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (RedirectToMobilePSTNAction.class.getName ());

    public RedirectToMobilePSTNAction ()
    {
	super (ActionType.RedirectToMobilePSTN);
    }

    @Override
    public Result execute (Call call, String...options)
    {
	// Do absolutely nothing--used to chain conditions
	return new Result ();
    }

    @Override
    public void print ()
    {
	log.debug ("\t\tRedirectToMobilePSTN");
    }
}
