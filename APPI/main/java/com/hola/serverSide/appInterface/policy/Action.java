package com.hola.serverSide.appInterface.policy;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  Hola
 */
import com.hola.serverSide.appInterface.call.ProtoCall;
import com.hola.serverSide.appInterface.common.Result;

public class Action
{
 // Logging
    private static final Logger log = LoggerFactory.getLogger (Action.class.getName ());

    private final ActionType actionType;

    public Action (ActionType _actionType)
    {
	this.actionType = _actionType;
    }

    public Result execute (ProtoCall call, String...options)
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
