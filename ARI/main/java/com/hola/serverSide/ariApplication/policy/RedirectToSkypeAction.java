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
import com.hola.serverSide.ariApplication.db.AccountHandlesHelper;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;

public class RedirectToSkypeAction extends Action
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (RedirectToSkypeAction.class.getName ());

    public RedirectToSkypeAction ()
    {
	super (ActionType.RedirectToSkype);
    }

    @Override
    public Result execute (Call call, String...options)
    {
	Result result = null;
	if (options [0].equals (Policy.EXECUTE_POLICY))
	    {
		String calleeUsernameOrEmail = call.getCalleeLegInfo ().getHolaAccount ().getUsernameOrEmail ();
		String skypeNumber = 
		    AccountHandlesHelper.getAccountHandlesByUsernameOrEmail (calleeUsernameOrEmail).getHandleForApp ("skype");
		if (skypeNumber != null)
		    {
			call.getCalleeLegInfo ().setHandle (skypeNumber);
			String query = CalleeUtils.getQueryStringToPlaceCall (call);
			result = 
			    CalleeUtils.originateCalleeChannelWithQueryStringAndBody (call, "PJSIP/" + skypeNumber + "@twilio-siptrunk", "");
		    }
		else
		    {
			log.error ("SkypeE164 redirect requested per policy, but SkypeE164 not found in handles/profile for {}",
				   calleeUsernameOrEmail);
		    }
		
		return result;
	    }
	else
	    {
		return new Result ();
	    }
    }

    @Override
    public void print ()
    {
	log.debug ("\t\tRedirectToSkype");
    }
}
