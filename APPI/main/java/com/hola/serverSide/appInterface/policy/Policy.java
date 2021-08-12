package com.hola.serverSide.appInterface.policy;

import java.util.List;
import java.util.ArrayList;

import com.hola.serverSide.appInterface.call.ProtoCall;
import com.hola.serverSide.appInterface.common.Result;

/**
 * A policy is made up of multiple policies and the associated actions. For example
 *
 * noSpamPolicy: if currentTime is in busyHoursOfTheDay
 *               and
 *               if caller is in group others
 *                  sendToVM
 *                   hangUp
 * In the above policy, there are two policy conditions, one has to do with 
 * checking time conditions, and the next one has to do with checking
 * if the user is in a particular group etc. There are also 2 actions.
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Policy
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (Policy.class.getName ());

    private String              name;
    private List <Condition>    conditions;
    private List <Action>       trueActions;
    private List <Action>       falseActions;
    private List <Availability> trueAvailabilities;
    private List <Availability> falseAvailabilities;
    private int                 numberOfTimesExecuted;

    public static final String COMPUTE_ACTION_PLAN = "COMPUTE_ACTION_PLAN";
    public static final String EXECUTE_POLICY = "EXECUTE_POLICY";

    public Policy (String _name, List <Condition> _conditions, List <Action> _trueActions, List <Action> _falseActions, 
		   List <Availability> _trueAvailabilities, List <Availability> _falseAvailabilities)
    {
	this.name                  = _name;
	this.conditions            = _conditions;
	this.trueActions           = _trueActions;
	this.falseActions          = _falseActions;
	this.trueAvailabilities    = _trueAvailabilities;
	this.falseAvailabilities   = _falseAvailabilities;
	this.numberOfTimesExecuted = 0;
    }

    public Policy (String _name)
    {
	this.name = _name;
	this.numberOfTimesExecuted = 0;
    }

    public Result execute (ProtoCall call, String...options)
    {
	Result result = null;
	ActionGroup actionGroup = new ActionGroup ();

	if (conditionsAreValid (call))
	    {
		for (Action action : this.trueActions)
		    {
			//log.debug ("Executing true action " + action.getType ());

			if (options [0].equals (Policy.COMPUTE_ACTION_PLAN))
			    {
				actionGroup.add (action);
			    }
			result = action.execute (call, options);
		    }
		this.incNumberOfTimesExecuted ();
	    }
	else
	    {
		for (Action action : this.falseActions)
		    {
			//log.debug ("Executing false action " + action.getType ());

			if (options [0].equals (Policy.COMPUTE_ACTION_PLAN))
			    {
				actionGroup.add (action);
			    }
			result = action.execute (call, options);
		    }
		this.incNumberOfTimesExecuted ();		
	    }

	if (options [0].equals (Policy.COMPUTE_ACTION_PLAN))
	    {
		//call.getActionPlan ().addActionGroup (actionGroup);
	    }
	return result;
    }

    public List <Availability> getReachability (ProtoCall call, String...options)
    {
	if (conditionsAreValid (call))
	    {
		log.debug ("Getting true availabilities for policy {}", name);
		return trueAvailabilities;
	    }
	else
	    {
		log.debug ("Getting false availabilities for policy {}", name);
		return falseAvailabilities;
	    }
    }

    public void setName (String _name)
    {
	this.name = _name;
    }

    public void setConditions (List <Condition> _conditions)
    {
	this.conditions = _conditions;
    }

    public void setTrueActions (List <Action> _actions)
    {
	this.trueActions = _actions;
    }

    public void setFalseActions (List <Action> _actions)
    {
	this.falseActions = _actions;
    }

    public void setTrueAvailabilities (List <Availability> _trueAvailabilities)
    {
	this.trueAvailabilities = _trueAvailabilities;
    }

    public void setFalseAvailabilities (List <Availability> _falseAvailabilities)
    {
	this.falseAvailabilities = _falseAvailabilities;
    }

    public String getName ()
    {
	return this.name;
    }

    public List <Condition> getConditions ()
    {
	return this.conditions;
    }

    public List <Action> getTrueActions ()
    {
	return this.trueActions;
    }

    public List <Action> getFalseActions ()
    {
	return this.falseActions;
    }

    public List <Availability> getTrueAvailabilities ()
    {
	return this.trueAvailabilities;
    }

    public List <Availability> getFalseAvailabilities ()
    {
	return this.falseAvailabilities;
    }

    public void incNumberOfTimesExecuted ()
    {
	this.numberOfTimesExecuted ++;
    }

    public int getNumberOfTimesExecuted ()
    {
	return this.numberOfTimesExecuted;
    }

    private boolean conditionsAreValid (ProtoCall call)
    {
	boolean allValid = true;
	for (Condition condition: this.conditions)
	    {
		// log.debug ("Condition ....");
		allValid &= condition.isValid (call);
	    }
	return allValid;
    }

    public void print ()
    {
	log.debug ("Policy name: " + this.name);
	for (Condition condition : conditions)
	    {
		condition.print () ;
	    }
	log.debug ("\t->True actions:");
	for (Action action : trueActions)
	    {
		action.print ();
	    }

	log.debug ("\t->False actions:");
	for (Action action : falseActions)
	    {
		action.print ();
	    }

	log.debug ("\t->True availabilities:");
	for (Availability trueAvailability: trueAvailabilities)
	    {
		trueAvailability.print ();
	    }
	log.debug ("\t->False availabilities:");
	for (Availability falseAvailability: falseAvailabilities)
	    {
		falseAvailability.print ();
	    }

    }
}
