package com.hola.serverSide.appInterface.policy;

import java.util.List;

import com.hola.serverSide.appInterface.call.ProtoCall;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean isIn (ProtoCall call)
    {
	boolean verity = false;
	for (String user : users)
	    {
		log.debug ("Checking user " + user + " with " + call.getCallerInfo ().getID ());
		if (user.equals (call.getCallerInfo ().getID ()))
		    {
			log.debug ("User in list....");
			verity = true;
		    }
	    }
	return verity;
    }

    @Override
    public boolean isNotIn (ProtoCall call)
    {
	boolean verity = false;
	for (String user : users)
	    {
		if (user.equals (call.getCalleeInfo ().getID ()))
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
