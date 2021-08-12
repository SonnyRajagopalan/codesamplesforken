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

public class TimeComparisonList extends ComparisonList
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (TimeComparisonList.class.getName ());

    private final List <Duration> durations;

    public TimeComparisonList (List <Duration> _durations)
    {
	this.durations = _durations;
    }

    @Override
    public boolean isIn (Call call)
    {
	boolean verity = false;
	for (Duration thisDuration : durations)
	    {
		log.debug ("Comparing call time " + call.getCurrentTimeAtCallee () + " to [" + 
				    thisDuration.getStart () + ", " + thisDuration.getEnd () + "]");
		verity |= thisDuration.timeInDuration (call.getCurrentTimeAtCallee ());
		log.debug ("\t\tverity = " + verity);

	    }
	log.debug ("\tReturning verity = " + verity);
	return verity;
    }

    @Override
    public boolean isNotIn (Call call)
    {
	return (!isIn (call));
    }

    @Override
    public void print ()
    {
	for (Duration thisDuration : durations)
	    {
		thisDuration.print ();
	    }
    }
}
