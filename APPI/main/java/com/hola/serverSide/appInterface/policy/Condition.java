package com.hola.serverSide.appInterface.policy;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.appInterface.call.ProtoCall;

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

    public boolean isValid (ProtoCall call)
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
