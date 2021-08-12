package com.hola.serverSide.appInterface.policy;

import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.appInterface.call.ProtoCall;

/**
 *
 * This class is used to create a policy object that compares current time to various
 * durations. In the general case, the Policy TimeCondition (PTC) is used to compute
 * the boolean outcome of expressions such as:
 * ptc1: if currentTime in {durationCollection}
 *     where 
 * durationCollection: [duration]+
 *     and
 * duration: [startTime, endTime]
 *
 * The semantics of duration includes both startTime and endTime.
 *
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */

public class TimeCondition extends Condition
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (TimeCondition.class.getName ());

    private final TimeComparisonList timeComparisonList; // to compare: return true if current time in 
                                                         // one of the durations
    private final String          compare;

    public TimeCondition (String _type, TimeComparisonList _timeComparisonList, String _compare)
    {
	super (_type);
	this.timeComparisonList = _timeComparisonList;
	this.compare            = _compare;
    }

    @Override
    public boolean isValid (ProtoCall call)
    {
	// Each time this policy executes, we should increment the static counter

	switch (this.compare)
	    {
	    case "IN":
		// Only needs to be in one of the durations
		return timeComparisonList.isIn (call);
	    case "NOT_IN":
		// Needs to NOT be in any of the durations
		return timeComparisonList.isNotIn (call);
	    default:
		// Error
	    }
	return false;
    }
    
    @Override
    public void print ()
    {
	log.debug ("\tTime condition: will check if the call [" + this.compare + 
			    "] these durations:");
	this.timeComparisonList.print ();
    }

}
