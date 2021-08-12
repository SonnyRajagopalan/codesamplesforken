/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
/*
 * 10.21.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! Call State Machine
 *
 * Singleton for WaitForOutgoingChannelRequestState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import java.util.List;
import java.io.IOException;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import java.io.UnsupportedEncodingException;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  Hola
 */
import com.hola.serverSide.ariApplication.common.Result;
import com.hola.serverSide.ariApplication.common.Color;
import com.hola.serverSide.ariApplication.call.LegInfo;
import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CalleeUtils;
import com.hola.serverSide.ariApplication.call.Location;
import com.hola.serverSide.ariApplication.ari.Channel;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.policy.Policy;
import com.hola.serverSide.ariApplication.policy.Action;
import com.hola.serverSide.ariApplication.policy.ContinueAction;
import com.hola.serverSide.ariApplication.policy.ActionPlan;
import com.hola.serverSide.ariApplication.policy.ActionGroup;
import com.hola.serverSide.ariApplication.policy.RejectAction;
import com.hola.serverSide.ariApplication.db.HolaAccountsHelper;
import com.hola.serverSide.ariApplication.db.AccountHandlesHelper;
import com.hola.serverSide.ariApplication.db.AccountPoliciesHelper;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;
import com.hola.serverSide.ariApplication.db.beans.AccountHandles;


public class CSMWaitForOutgoingChannelRequestState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForOutgoingChannelRequestState.class.getName ());
    // Eager initialization
    private static final CSMWaitForOutgoingChannelRequestState instance = new CSMWaitForOutgoingChannelRequestState  ();

    private CSMWaitForOutgoingChannelRequestState ()
    {
	setState (CSMState.WaitForOutgoingChannelRequest);
    }
    
    public static CSMWaitForOutgoingChannelRequestState getInstance ()
    {
	return instance;
    }

    @Override
    public void processExtensionToExternalCall (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }


    @Override
    public void processOutgoingMappingFound (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
	//
	//processPoliciesForCall (call, event);
	//
        process (call, event);
    }
    
    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError (event);
		break;
	    case OutgoingMappingNotFound:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingMapping));
		call.processOutgoingMappingNotFound (event);
		break;
	    case OutgoingChannelAvailable:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelAnswer));
		return; // Wait for the outgoing channel to answer
	    case OutgoingChannelHungup:
		call.changeState (getStateForStateName (CSMState.WaitForOutgoingHangupProcessing));
		call.processOutgoingHangup (event);
	    default:
		// Error event?
	    }
    }
	
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	// Nothing for now
	// Here, the event itself is processed and then the type of event is deduced

	/*
	  1. get the callee
	  2. deduce the policy for the callee (option "DRY_RUN"), record action plan
	  3. start plan-A for the callee:
	  3a    if continue, do everything already here
          3b    if redirect, then change callee and do everything already here
          3c    if send sms/text/ip message/email, then return CSMEvent.SendSMS etc. and capture 
	           the state transition in process ().
          3d    if reject, then return CSMEvent.Reject and capture the move to 
	           WaitForOutgiongHangup in process ()
	 */

	// Now, construct the request for the channel origination
	// Here's a sample:
	// http://localhost:8088/ari/channels?app=psa&endpoint=PJSIP%2Fbob&appArgs=dialed

	Result result = null;
	ActionGroup actionGroupToExecuteHere = null;

	if (call.getActionPlan ().getIndex () == 0)
	    {
		setCalleeLegInfoAndActionPlan (call, event);
	    }

	actionGroupToExecuteHere = call.getActionPlan ().getActionGroups ().get (call.getActionPlan ().getIndex ());
	call.getActionPlan ().incIndex (); // in case this exection does not work, you need to point to the next plan

	if (call.getCalleeLegInfo () == null)
	    {
		log.error ("Callee leg can't be null");
		return CSMEvent.Error;
	    }
	else if (CalleeUtils.actionGroupContainsReject (actionGroupToExecuteHere)) 
	    {
		// do everyting you need for SMS, VM or WhatsApp etc. and then close the call. 
		// For now, we just assume Reject is the only action to be performed here
		return CSMEvent.OutgoingChannelHungup;
	    }

	for (Action action: actionGroupToExecuteHere.getActions ())
	    {
		result = action.execute (call, Policy.EXECUTE_POLICY);

		switch (result.getCode ().getTypeString ())
		    {
			// TBD. It is not clear how to chain return codes.
		    case "Success":
			log.debug ("Policy action {} successfully executed", action.getType ());
			break;
		    case "Error":
			log.debug ("Policy action {}: error in execution!", action.getType ());
			break;
		    }
	    }

	if (result != null)
	    {
		return result.getEvent ();
	    }
	else
	    {
		return CSMEvent.Error;
	    }
    }

    public void setCalleeLegInfoAndActionPlan (Call call, Event event)
    {
	LegInfo calleeLeg = getCalleeLegInfo (call, event);
	//Person callee = Person.getPersonFromHandle (call.getCallee ());
	
	if (calleeLeg != null)
	    {
		//Person employee = calleeLeg.getPerson ();
		HolaAccount employee = calleeLeg.getHolaAccount ();
		if (employee != null)
		    {
			List <Policy> calleePolicies = 
			    AccountPoliciesHelper.getPolicyListForUsernameOrEmail (employee.getUsernameOrEmail ());
			if (calleePolicies.size () != 0)
			    {
				log.debug ("Process policies for call {}", call.getUUID ());
				
				for (Policy policy: calleePolicies)
				    {
					log.debug ("executing policy " + policy.getName ());
					Result result = policy.execute (call, Policy.COMPUTE_ACTION_PLAN); // don't process result
				    }
			    }
			else
			    {
				// Callee then is either an employee with no policies, or
				// a callee that is outside the Hola! zone (perhaps an external number).
				log.debug ("No callee policies -- taking the default route");
			    }
		    }
		else // yes, employee == null
		    {
			log.debug ("Calling non-employee {}", calleeLeg.getHandle ());
			ActionGroup actionGroup = new ActionGroup ();
			actionGroup.add (new ContinueAction ());
			call.getActionPlan ().addActionGroup (actionGroup);
		    }
		  
	    }
	else
	    {
		log.debug ("\n\n\n\n\t\t\tCallee leg is null!!\n\n\n\n\n");
	    }
	call.setCalleeLegInfo (calleeLeg);
    }

    private LegInfo getCalleeLegInfo (Call call, Event event)
    {
	LegInfo calleeLeg = null;
	if (call.getLastCSMEvent () == CSMEvent.ExtensionToExtensionCall)
	    {
		StasisStart stasisStart = (StasisStart) event;
		calleeLeg = new LegInfo ();
		// getPersonFromPJSIPHandle ("PJSIP/Bob")
		// StasisStart contents: ... "args":["inbound","6001","PJSIP/sonny"]...
		//calleeLeg.setHolaAccount (Person.getPersonFromPJSIPHandle (stasisStart.getArgs ().get (2))); 
		// CHECK CHECK CHECK
		int extension = Integer.parseInt (stasisStart.getArgs ().get (1));
		calleeLeg.setHolaAccount (HolaAccountsHelper.getHolaAccountByExtension (extension)); 
		calleeLeg.setHandle (stasisStart.getArgs ().get (2));
		call.setCalleeQueryString (stasisStart.getArgs ().get (2));
		String pjsippedAppHandle = 
		    AccountHandlesHelper.getPJSIPpedAppHandleForFirstVoiceTech (HolaAccountsHelper.getHolaAccountByExtension
										(extension).getUsernameOrEmail ());
		System.out.println (Color.Red + "calleeQueryString will be " + pjsippedAppHandle + Color.End);
		call.setCalleeQueryString (pjsippedAppHandle);
		calleeLeg.setHandle (pjsippedAppHandle);
	    }
	else if (call.getLastCSMEvent () == CSMEvent.ExtensionToExternalCall)
	    {
		StasisStart stasisStart = (StasisStart) event;
		calleeLeg = new LegInfo ();
		//return stasisStart.getArgs ().get (2); // PJSIP/+17817771212@twilio-siptrunk
		// getPersonFromHandle ("+17817771212");
		//calleeLeg.setPerson (Person.getPersonFromHandle (stasisStart.getArgs ().get (2).substring (6, 18))); // getPersonFromHandle () returns null for non-employees
		// CHECK CHECK CHECK
		String someHandle = stasisStart.getArgs ().get (2).substring (6, 18); // +17817771212
		calleeLeg.setHolaAccount (AccountHandlesHelper.getHolaAccountFromHandle (someHandle)); 
		calleeLeg.setHandle (stasisStart.getArgs ().get (2)); // PJSIP/+17817771212@twilio-siptrunk
		call.setCalleeQueryString (stasisStart.getArgs ().get (2)); // PJSIP/+17817771212@twilio-siptrunk
	    }
	else if (call.getLastCSMEvent () == CSMEvent.FromPSTNForNewCall) // Can be an internal or external call
	    {
		ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;

		//System.out.println (Color.DarkGreen + "DTMF = " + call.getCallerDTMFReceived ().substring (0, 1) +
		//		    " and size = " + call.getCallerDTMFReceived ().length () + Color.End);
		calleeLeg = new LegInfo ();
		// Person.getPersonFromHandle ( "+" + 917817771212.substring (1))

		if (call.getCallerDTMFReceived ().substring (0, 2).equals ("91")) // Must be employee
		    {
			// CHECK CHECK CHECK
			//calleeLeg.setPerson (Person.getPersonFromHandle ("+" + call.getCallerDTMFReceived ().substring (1))); //
			String someHandle = "+" + call.getCallerDTMFReceived ().substring (1);
			calleeLeg.setHolaAccount (AccountHandlesHelper.getHolaAccountFromHandle (someHandle)); 
			calleeLeg.setHandle ("+" + call.getCallerDTMFReceived ().substring (1));
			call.setCalleeQueryString ("PJSIP/+" + call.getCallerDTMFReceived ().substring (1) + "@twilio-siptrunk");

			// In the case of an external call, the callee ID is the same as the E164
			//System.out.println (Color.Red + "Before: " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()) + Color.End);
			// ActiveCallsHelper.updateActiveCallWithCalleeID (call.getUUID (),
			// 						"+" + call.getCallerDTMFReceived ().substring (1));
			String calleeID=calleeLeg.getHandle ();
			HolaAccount calleeHolaAccount = AccountHandlesHelper.getHolaAccountFromHandle (calleeLeg.getHandle ());

			if (calleeHolaAccount != null)
			    {
				calleeID = Integer.toString (calleeHolaAccount.getExtension ());
			    }
			
			ActiveCallsHelper.updateActiveCallWithCalleeIDAndHandle (call.getUUID (),
										 /* BUGFIX START FOR: 0000002, Sonny Rajagopalan */
										 calleeID,
										 //"+" + call.getCallerDTMFReceived ().substring (1),
										 /* BUGFIX END FOR: 0000002, Sonny Rajagopalan */
										 "+" + call.getCallerDTMFReceived ().substring (1));
			//System.out.println (Color.BrightGreen + "After:  " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()) + Color.End);
			call.setCallee ("+" + call.getCallerDTMFReceived ().substring (1));
		    }
		else if (call.getCallerDTMFReceived ().substring (0, 1).equals ("6") && 
			 (call.getCallerDTMFReceived ().length () == 4))
		    {
			String extension = call.getCallerDTMFReceived ();
			HolaAccount employee = AccountHandlesHelper.getHolaAccountFromHandle (extension);
			calleeLeg.setHolaAccount (employee); //
			calleeLeg.setHandle ("PJSIP/" + employee.getExtension ());
			call.setCalleeQueryString ("PJSIP/" + employee.getExtension ());

			// In the case of an internal call, the callee ID is the same as the extension
			//System.out.println (Color.Red + "Before: " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()) + Color.End);
			ActiveCallsHelper.updateActiveCallWithCalleeIDAndHandle (call.getUUID (),
										 call.getCallerDTMFReceived (),
										 call.getCallerDTMFReceived ());
			//System.out.println (Color.BrightGreen + "After:  " + ActiveCallsHelper.getActiveCallByUUID (call.getUUID ()) + Color.End);
			call.setCallee (call.getCallerDTMFReceived ());
		    }
	    }
	return calleeLeg;
    }
}
