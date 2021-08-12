package com.hola.serverSide.appInterface.policy;

import java.util.List;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.appInterface.call.ProtoCall;

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
    public boolean isIn (ProtoCall call)
    {
	boolean verity = false;
	for (Duration thisDuration : durations)
	    {
		log.debug ("Comparing call time " + call.getCalleeInfo ().getCurrentTime ()  + " to [" + 
				    thisDuration.getStart () + ", " + thisDuration.getEnd () + "]");
		verity |= thisDuration.timeInDuration (call.getCalleeInfo ().getCurrentTime ());
		log.debug ("\t\tverity = " + verity);

	    }
	log.debug ("\tReturning verity = " + verity);
	return verity;
    }

    @Override
    public boolean isNotIn (ProtoCall call)
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
