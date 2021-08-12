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
import com.hola.serverSide.ariApplication.common.Result;

public class SendTextToVoIPAction extends Action
{
    public SendTextToVoIPAction ()
    {
	super (ActionType.SendTextToVoIP);
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
	System.out.println ("\t\tSendTextToVoIP");
    }
}
