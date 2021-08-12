/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.policy;

import java.util.List;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.call.Call;

public class UserComparisonList extends ComparisonList
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (UserComparisonList.class.getName ());

    private final List <String> users;

    public UserComparisonList (List <String> _users)
    {
	this.users = _users;
    }

    @Override
    public boolean isIn (Call call)
    {
	boolean verity = false;
	for (String user : users)
	    {
		log.debug ("Checking user " + user + " with " + call.getCaller ());
		if (user.equals (call.getCaller ()))
		    {
			log.debug ("User in list....");
			verity = true;
		    }
	    }
	return verity;
    }

    @Override
    public boolean isNotIn (Call call)
    {
	boolean verity = false;
	for (String user : users)
	    {
		if (user.equals (call.getCallee ()))
		    {
			verity = true;
		    }
	    }
	return verity;
    }

    @Override
    public void print ()
    {
	for (String user : users)
	    {
		log.debug ("\t" + user);
	    }
    }
}
