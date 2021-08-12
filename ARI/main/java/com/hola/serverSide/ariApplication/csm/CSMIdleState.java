/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 *
 * 10.21.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! Call State Machine
 *
 * Singleton for IdleState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.common.ThreadUtils;
import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.call.CallDB;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.StasisEnd;
import com.hola.serverSide.ariApplication.ari.ChannelStateChange;
import com.hola.serverSide.ariApplication.ari.ChannelEnteredBridge;
import com.hola.serverSide.ariApplication.ari.ChannelLeftBridge;
import com.hola.serverSide.ariApplication.ari.ChannelVarset;
import com.hola.serverSide.ariApplication.ari.ChannelConnectedLine;
import com.hola.serverSide.ariApplication.ari.ChannelHangupRequest;
import com.hola.serverSide.ariApplication.ari.ChannelDestroyed;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.ari.EndpointStateChange;
import com.hola.serverSide.ariApplication.ari.ARITransactionType;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.HolaAccountsHelper;
import com.hola.serverSide.ariApplication.db.AccountHandlesHelper;
import com.hola.serverSide.ariApplication.db.ActiveHandoversHelper;
import com.hola.serverSide.ariApplication.db.beans.ActiveCall;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;
import com.hola.serverSide.ariApplication.db.beans.ActiveHandover;
import com.hola.serverSide.ariApplication.common.Color;

public class CSMIdleState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMIdleState.class.getName ());
    // Eager initialization
    private static final CSMIdleState instance = new CSMIdleState  ();

    private CSMIdleState ()
    {
	setState (CSMState.Idle);
    }
    
    public static CSMIdleState getInstance ()
    {
	return instance;
    }

    @Override
    public void processReceivedCallEvent (Event event)
    {
	/* 
	 * This is where we create a new call or establish a previously existing call context.
	 * Critically important that the call context is either created here (by instantiating 
	 * a Call object using new) or established by looking up the caller or callee of the 
	 * call. As of the current version of the code, it is not clear what should happen if
	 * the user so chooses to create a separate call while on a call.
	 */

	Call call = null;
	CSMEvent transition = CSMEvent.UNKNOWN;

	/*
	 * First, establish the call context
	 */
	ARITransactionType tType = ARITransactionType.getTransactionTypeIntFromString (event.getType ());
	switch (tType)
	    {
	    case StasisStart:
		call = getCallContextFromStasisStart (event);
		break;
	    case StasisEnd:
		call = getCallContextFromStasisEnd (event);
		break;
	    case ChannelStateChange:
		call = getCallContextFromChannelStateChange (event);
		return; // Don't want a valid call, but incorrect state transition breaking everything
		//break;
	    case ChannelHangupRequest:
		call = getCallContextFromChannelHangupRequest (event);
		//break; // Don't want a valid call, but incorrect state transition breaking everything
		return; // Don't want a valid call, but incorrect state transition breaking everything
		//break;
	    case ChannelDestroyed:
		// The following commented out after interfering with the cleanup activity
		// triggered by OutgoingChannelHangup or IncomingChannelHangup. The processing
		// triggered by Outgoing/IncomingChannelHangup is enough; no need to reap this
		// event and process it correctly.
		//call = getCallContextFromChannelDestroyed (event); 
		break;
	    case ChannelDtmfReceived:
		processChannelDtmfReceived (event);
		return; // because there's nothing else to do
	    case ChannelEnteredBridge:
		break;
	    case ChannelLeftBridge:
		break;
	    case ChannelVarset:
		break;
	    case ChannelConnectedLine:
		break;
	    case PlaybackStarted:
		break;
	    case PlaybackFinished:
		break;
	    case EndpointStateChange:
		// call = getCallContextFromEndpointStateChange (event); Not the processing route we are taking 
		//                                                        for the trial
		break;
	    }
	/*
	 * Next, make the correct state transition so that the SM can pick it up from there
	 */
	if (call != null)
	    {
		makeCorrectTransitions (call, event);
	    }
	else
	    {
		log.error ("Call context could not be established for this event {}", event);
	    }
    }
    
    private Call getCallContextFromStasisStart (Event event)
    {
	StasisStart stasisStart = (StasisStart) event;
	String typeOfIncoming = stasisStart.getArgs ().get (0);
	Call call = null;

	switch (typeOfIncoming)
	    {
	    case "inbound": // "args":["inbound","6001","PJSIP/6001"]
		/*
		  E.g.:

		  {"type":"StasisStart","application":"psa","timestamp":1456017392657,
		  "args":["inbound","6001","PJSIP/6001"],"channel":{"accountcode":"",
		  "caller":{"name":"6002","number":"6002"},"connected":{"name":"","number":""},
		  "creationtime":1456017392657,"dialplan":{"context":"from-internal","exten":"6001",
		  "priority":1},"id":"1456017392.455","language":"en","name":"PJSIP/6002-000000d3",
		  "state":"Ring"},"replace_channel":null}
		  
		  WARNING: will receive this for a HandIn as well (in that case a call already exists!)
		 */
	    case "external": // "args":["external","+13022290507","PJSIP/+13022290507@twilio-siptrunk"]
		/*
		  E.g.:

		  {"type":"StasisStart","application":"psa","timestamp":1456017591151,
		  "args":["external","+13022290507","PJSIP/+13022290507@twilio-siptrunk"],
		  "channel":{"accountcode":"","caller":{"name":"6002","number":"6002"},"connected":{"name":"","number":""},
		  "creationtime":1456017591150,"dialplan":{"context":"from-internal","exten":"913022290507",
		  "priority":2},"id":"1456017591.458","language":"en","name":"PJSIP/6002-000000d5","state":"Ring"},
		  "replace_channel":null}

		 */
		
		{
		    String callerNumber = stasisStart.getChannel ().getCaller ().getNumber ();
		    String calleeNumber = stasisStart.getArgs ().get (1);
		    log.debug ("Thread ID " + Thread.currentThread ().getId () + 
					" -- This is an inbound call from caller " + 
					stasisStart.getChannel ().getCaller ().getNumber () + " and the callee is extension " + 
					stasisStart.getArgs ().get (1) + " and SIP " + stasisStart.getArgs ().get (2));
	    
		    call = CallDB.getActiveCallFromCallerNumberAndCalleeNumber (callerNumber, calleeNumber);
		    if ((call == null) && (stasisStart.getArgs ().get (0).equals ("inbound")))
			{
			    /*
			      Check if this a handin- scenario: for any call, both the original caller or callee
			      can initiate a handin.
			      SUSPECT SUSPECT SUSPECT

			      Here are the issues I suspect will need more design time:

			      1.  It  is  not  clear  how this  is  going  to  work  under
			         concurrency situations. Imagine  both original caller and
			         callee   on  a   call   making   hand-ins  and   handouts
			         simultaneously.

			      2. Will this work correctly when  one of caller or callee is
			         a  non-employee?  Wait:  a  non-employee  cannot  perform
			         handin or handout.
				 
				    a.  A scenario  exists where  a call  starts its  life
				 between  (Alice NonEmployee)  and  (Bob  Employee) as  an
				 ExtensionToExternal  call, but  clearly Bob  is the  only
				 person who is permitted to do handouts or handins in this
				 situation.

				    b.  If  Alice drops  and wants to  call back,  that is
				 StasisEnd  scenario that  has to  be reaped  before Alice
				 calls back.

				 (SUSPECT SUSPECT  SUSPECT) 

				    c. (THIS SCENARIO NEEDS WORK, but not because our call
				 checking model is broken, it  is because we don't support
				 call hold  yet.) Lastly,  if Alice  performs a  call hold
				 with Bob,  and then places  a call  to the DID,  and then
				 punches in the DTMF for Bob, is that supposed to work? Is
				 that a legitimate user story?

			    */ 
			    call = CallDB.getActiveCallFromCallerNumberAndCalleeNumber (calleeNumber, callerNumber);
			}

		    if (call == null)
			{
			    log.debug ("Thread ID " + Thread.currentThread ().getId () + " call is null");
			    if ((stasisStart.getChannel ().getDialplan ().getExten ().length () == 4) && // Should be configurable
				(stasisStart.getChannel ().getCaller ().getNumber ().length () == 4))
				{
				
				    log.debug ("Thread ID " + Thread.currentThread ().getId () + 
							"Caller = " + stasisStart.getChannel ().getCaller ().getNumber () + 
							" and callee is " + stasisStart.getArgs ().get (1));
				    call = new Call (stasisStart.getChannel ().getCaller ().getNumber (), 
						     stasisStart.getArgs ().get (1), CSMEvent.ExtensionToExtensionCall);
				    call.addToCallerChannels (stasisStart.getChannel ());

				    CallDB.addACallIntoDB (call);
				}
			    else if (typeOfIncoming.equals ("external") && 
				     (stasisStart.getChannel ().getCaller ().getNumber ().length () == 4)) // Caller is employee
				{
				    log.debug ("2");
				    call = new Call (stasisStart.getChannel ().getCaller ().getNumber (), 
						     stasisStart.getArgs ().get (1), CSMEvent.ExtensionToExternalCall);
				    call.addToCallerChannels (stasisStart.getChannel ());
				    CallDB.addACallIntoDB (call);
				}
			    else 
				{
				    log.debug ("3");
				    call = new Call (stasisStart.getChannel ().getCaller ().getNumber (), 
						     stasisStart.getArgs ().get (1), CSMEvent.UNKNOWN);
				    CallDB.addACallIntoDB (call);
				}
			    
			    /* BUGFIX START FOR: 0000002, Sonny Rajagopalan */
			    String calleeID = stasisStart.getArgs ().get (1);
			    HolaAccount calleeHolaAccount 
				= AccountHandlesHelper.getHolaAccountFromHandle (stasisStart.getArgs ().get (1));

			    if (calleeHolaAccount != null)
				{
				    calleeID = Integer.toString (calleeHolaAccount.getExtension ());
				    call.setCallee (calleeID);
				}
			    /* BUGFIX END FOR: 0000002, Sonny Rajagopalan */
			    ActiveCall activeCall = new ActiveCall (call.getUUID (),
								    stasisStart.getChannel ().getId (),
								    stasisStart.getChannel ().getCaller ().getNumber (),
								    stasisStart.getChannel ().getCaller ().getNumber (),
								    "CALLEE_CHANNEL",  // update at "dialed" StasisStart event
								    /* BUGFIX START FOR: 0000002, Sonny Rajagopalan */
								    calleeID,
								    /* BUGFIX END FOR: 0000002, Sonny Rajagopalan */
								    //call.getCallee (),
								    stasisStart.getArgs ().get (1),
								    false);
			    ActiveCallsHelper.putInActiveCallsTable (activeCall);
			}
		    else
			{
			    HolaAccount employee = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (callerNumber));
			    String handoverRequestor = "TBD";
			    // Get the existing call
			    log.debug ("Thread ID " + Thread.currentThread ().getId () + " call is NOT null");
			    /*
			      ARGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			      Not supposed to happen here.
			      Remember, CSMWaitForUpdateActiveCallDBState does
			      ALL, ALL of the channel maintenance properly
			      ARGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			      call.addToCallerChannels (stasisStart.getChannel ());
			    */
			    call.setLastCSMEvent (CSMEvent.SIPLegHandInForActiveCallRequested);
			    
			    if (employee == null) // An "inbound" type call can only be from an employee!
				{
				    log.error ("Serious error. Non-employee in inbound StasisStart event: {}!",
					       stasisStart.toJsonString ());
				}

			    if (employee.getExtension () == Integer.parseInt (call.getCaller ()))
				{
				    handoverRequestor = "caller";
				}
			    else if (employee.getExtension () == Integer.parseInt (call.getCallee ()))
				{
				    handoverRequestor = "callee";
				}
				
			    ActiveHandover activeHandover = 
				new ActiveHandover (call.getUUID (), 
						    employee.getExtension (),
						    Integer.toString (employee.getExtension ()),
						    stasisStart.getChannel ().getCaller ().getNumber (),
						    stasisStart.getChannel ().getId (),
						    handoverRequestor);
			    
			    ActiveHandoversHelper.putInActiveHandoversTable (activeHandover);
			}

		    log.debug ("STASISSTART:= {}", stasisStart.toJsonString ());
		    

		}
	    break;
	    case "dialed": // The best place to populate callee information
		/*
		  E.g.:

		  {"type":"StasisStart","application":"psa","timestamp":1456017395808,
		  "args":["dialed"],"channel":{"accountcode":"","caller":{"name":"Sonny","number":"6001"},
		  "connected":{"name":"Sonny","number":""},"creationtime":1456017393798,
		  "dialplan":{"context":"from-internal","exten":"s","priority":1},"id":"1456017393.456",
		  "language":"en","name":"PJSIP/6001-000000d4","state":"Up"},"replace_channel":null}
		 */
		log.debug ("Thread ID " + Thread.currentThread ().getId () + " This is an dialed call");
		call = CallDB.getCallFromCalleesChannelID (stasisStart.getChannel ().getId ());
		if (call != null)
		    {
			call.setLastCSMEvent (CSMEvent.CalleeProcessing);
		    }
		else
		    {
			log.debug ("Call was removed (likely by caller channel hanging up); " + 
				   "Silently ignoring StasisStart event from callee.");
		    }
		/*
		  Not the correct place to do this!
		  ActiveCallsHelper.updateActiveCallWithNewCalleeInfo (call.getUUID (), 
		  stasisStart.getChannel ().getId (),
		  stasisStart.getChannel ().getCaller ().getNumber ());
		*/
		break;
	    case "did": // A person is calling from outside the VoIP island and trying to reach someone within the island
		        // or, in the case of an employee, using the enterprise's long distance connectivity to call
		        // somebody else on an E164.
		/*
		  E.g.:
		  {"type":"StasisStart","application":"psa","timestamp":1456017897158,
		  "args":["did","+13022290507"],"channel":{"accountcode":"","caller":{"name":"","number":"+13022290507"},
		  "connected":{"name":"","number":""},"creationtime":1456017897157,
		  "dialplan":{"context":"from-external","exten":"+17812096264","priority":2},"id":"1456017897.460",
		  "language":"en","name":"PJSIP/twilio-siptrunk-000000d7","state":"Ring"},"replace_channel":null}
		 */
		log.debug ("Thread ID " + Thread.currentThread ().getId () + " This is an did call from caller " + 
				    stasisStart.getChannel ().getCaller ().getNumber ());
		/*
		 * if employee:
		 *   if she has call going on: // Assume she is requesting handout
		 *      get the callee
		 *      perform the handout
		 *   else:
		 *      expect DTMF for figuring out who to call--internal or external allowed
		 * else:
		 *   expect DTMF for figuring out who to call--only internal allowed
		 */
		
		String incomingPSTNHandle = stasisStart.getChannel ().getCaller ().getNumber (); // +17815551212
		// Person employee = Person.getPersonFromHandle (incomingPSTNHandle);
		HolaAccount employee = AccountHandlesHelper.getHolaAccountFromHandle (incomingPSTNHandle);
		
		if (employee != null) // Yes, the incoming PSTN call is from an employee
		    {
			//call = CallDB.getCallFromANumber (employee.getID ());
			int employeeExtension = employee.getExtension ();
			/* BUGFIX START FOR: 0000017, Sonny Rajagopalan */
			ActiveCall activeCall = ActiveCallsHelper.getActiveCallByExtension (employeeExtension);
			if (activeCall != null)
			    {
				call = CallDB.getCallFromANumber (Integer.toString (employeeExtension));
				/* BUGFIX END FOR: 0000017, Sonny Rajagopalan */
				if (call != null) // Yes, an active call going on
				    {
					String handoverRequestor = "TBD";
					call.setLastCSMEvent (CSMEvent.FromPSTNForExistingCall);
					if (call.getCaller ().equals (Integer.toString (employeeExtension)))
					    {
						handoverRequestor = "caller";
					    }
					else if (call.getCallee ().equals (Integer.toString (employeeExtension)))
					    {
						handoverRequestor = "callee";
					    }
					else
					    {
						log.error ("Handover requestor (should be an employee), {} is neither callee nor caller!",
							   employeeExtension);
					    }
					// Active call management will be done elsewhere
					ActiveHandover activeHandover = 
					    new ActiveHandover (call.getUUID (), 
								employee.getExtension (),
								Integer.toString (employee.getExtension ()),
								stasisStart.getChannel ().getCaller ().getNumber (),
								stasisStart.getChannel ().getId (),
								handoverRequestor);
					
					ActiveHandoversHelper.putInActiveHandoversTable (activeHandover);
					
				    }
			/* BUGFIX START FOR: 0000017, Sonny Rajagopalan */
			    }
			/* BUGFIX END FOR: 0000017, Sonny Rajagopalan */
			else // No active call for this employee
			    {
				// Set caller, no idea (yet) re: callee.
				// Don't set the caller ID to be incomingPSTNHandle--getCallFromCallerNumber will break.
				call = new Call (Integer.toString (employee.getExtension ()), "TBD", CSMEvent.FromPSTNForNewCall);
				call.addToCallerChannels (stasisStart.getChannel ());
				CallDB.addACallIntoDB (call);

				activeCall = new ActiveCall (call.getUUID (),
							     stasisStart.getChannel ().getId (),
							     //stasisStart.getChannel ().getCaller ().getNumber (),
							     Integer.toString (employee.getExtension ()),
							     stasisStart.getChannel ().getCaller ().getNumber (),
							     "CALLEE_CHANNEL", 
							     call.getCallee (), // Callee ID, which was set to TBD earlier
							     //stasisStart.getArgs ().get (1), wrong. 
							     "TBD", // Callee handle
							     false);
				ActiveCallsHelper.putInActiveCallsTable (activeCall);
			    }

		    }
		else // No, this PSTN number is not an employee
		    {
			// Set the event to process as the same as employee-no active call,
			// but the distinction that this call is from a non-employee
			// will be made in the WaitForDTMF state
			call = new Call (incomingPSTNHandle, "TBD", CSMEvent.FromPSTNForNewCall);
			call.addToCallerChannels (stasisStart.getChannel ());
			CallDB.addACallIntoDB (call);


			ActiveCall activeCall = new ActiveCall (call.getUUID (),
								stasisStart.getChannel ().getId (),
								stasisStart.getChannel ().getCaller ().getNumber (),
								stasisStart.getChannel ().getCaller ().getNumber (),
								"CALLEE_CHANNEL", 
								call.getCallee (), // Callee ID, which was set to TBD earlier
								//stasisStart.getArgs ().get (1), wrong
								"TBD", // Callee handle
								false);
			ActiveCallsHelper.putInActiveCallsTable (activeCall);
		    }
		
		break;
	    default:
	    }

	return call;
    }
    
    private Call getCallContextFromStasisEnd (Event event)
    {
	StasisEnd stasisEnd = (StasisEnd) event;

	String channelThatHungup = stasisEnd.getChannel ().getId ();

	/*
	  First, check if this channel belongs to any call
	 */
	ActiveCall activeCall = ActiveCallsHelper.getActiveCallFromAChannelID (stasisEnd.getChannel ().getId ()); 

	if (activeCall == null)
	    {
		return null;
	    }
	System.out.println (Color.Red + "\tStasisEnd processing: Checking if " + channelThatHungup + 
			    " belongs in activeCall: " + activeCall.toJsonString () + Color.End);
	/*
	  So, this channel DOES belong to a call. Now, check if this thread is the first thread to reap this call
	 */
	if (ActiveCallsHelper.findAndSetCallZombieStatus (channelThatHungup))
	    {
		log.debug ("Other channel managed the StasisEnd related call reaping. Silently ignoring StasisEnd");
		return null;
	    }

	Call call = CallDB.getCallFromUUID (activeCall.getUUID ());

	if (call == null)
	    {
		// Likely a StasisEnd for a channel no longer associated with a call.
		return null;
	    }
	else
	    {
		if (activeCall.getCalleeChannel ().equals ("CALLEE_CHANNEL")) // As yet unallocated callee channel
		    {
			//CHECK CHECK CHECK
			call.removeFromCallerChannels (channelThatHungup);
			call.setLastCSMEvent (CSMEvent.IncomingChannelHungup);
			log.debug ("\n\n\nIncomingChannelHungup\n\n\n" + 
				   ThreadUtils.getThreadIDedStackTraceString () + "\n\n\n\n\n\n");

			/* BUGFIX START FOR: 0000022, Sonny Rajagopalan */
			// Callee channel can either be unallocated, or allocated, but the stasisStart has not happened
			// i.e., the callee has not picked up. In either case, remove the call, and return null
			// And make sure that if StasisStart comes in for this call (from the callee), the zombie
			// status is checked, and the event is ignored.
			CallDB.removeACallFromDB (call.getUUID ());
			ActiveCallsHelper.removeUnfinishedCallByCallerChannel (channelThatHungup);
			//
			return null;
			/* BUGFIX END FOR: 0000022, Sonny Rajagopalan */
		    }
	    }

	//call.printObject ();

	if (call.isCallerChannel (channelThatHungup))
	    {
		//call = CallDB.getCallFromCallersChannelID (channelThatHungup);
		call.removeFromCallerChannels (channelThatHungup);
		if (call.getCurrentState ().getStateName ().equals ("WaitForOutgoingChannelAnswer"))
		    {
			// This deletes the previously zombie'd call
			ActiveCallsHelper.findAndSetCallZombieStatus (channelThatHungup);
		    }
		call.setLastCSMEvent (CSMEvent.IncomingChannelHungup);
		log.debug ("\n\n\nIncomingChannelHungup\n\n\n" + 
				    ThreadUtils.getThreadIDedStackTraceString () + "\n\n\n\n\n\n");
	    }
	else if (call.isCalleeChannel (channelThatHungup))
	    {
		//call = CallDB.getCallFromCalleesChannelID (channelThatHungup);
		call.removeFromCalleeChannels (channelThatHungup);
		call.setLastCSMEvent (CSMEvent.OutgoingChannelHungup);
		log.debug ("\n\n\nOutgoingChannelHungup (getCallContextFromStasisEnd)\n\n\n" +
				    ThreadUtils.getThreadIDedStackTraceString () + "\n\n\n\n\n\n");
	    }
	else
	    {
		log.error ("Received a StasisEnd event for channel {} (name = {}, number = {}) for a call I don't know",
			   channelThatHungup, stasisEnd.getChannel ().getName (),
			   stasisEnd.getChannel ().getCaller ().getNumber ());
	    }
	return call;
    }
	
    private Call getCallContextFromChannelStateChange (Event event)
    {
	log.debug ("Thread ID " + Thread.currentThread ().getId () + " ChannelStateChange processing");
	ChannelStateChange channelStateChange = (ChannelStateChange) event;

	// Following added to fix the mobility bug /deadlock owing to ChannelStateChange processing when StasisStart 
	// is enough of a handout trigger...
	if (channelStateChange.getChannel ().getState ().equals ("Up"))
	    {
		return null;
	    }

	Call call = CallDB.getCallFromCalleesChannelID (channelStateChange.getChannel ().getId ());
	
	if ((call != null) &&
	    (channelStateChange.getChannel ().getState ().equals ("Ringing")))
	    {
		log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
		/*
		  Since we moved to the more recent paradigm of ContinueAction etc.,
		  this state won't be necessary, I think.
		call.setLastCSMEvent (CSMEvent.OutgoingChannelRing);
		*/
		return call;
	    }
	else
	    {
		return null;
	    }
    }

    private Call getCallContextFromChannelDestroyed (Event event)
    {
	log.debug ("Thread ID " + Thread.currentThread ().getId () + " ChannelDestroyed processing");
	ChannelDestroyed channelDestroyed = (ChannelDestroyed) event;

	Call call = CallDB.getCallFromAChannelID (channelDestroyed.getChannel ().getId ());

	if (call != null)
	    {
		log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
		call.setLastCSMEvent (CSMEvent.OutgoingChannelHungup);
		return call;
	    }
	else
	    {
		return null;
	    }
    }

    private Call getCallContextFromEndpointStateChange (Event event)
    {
	Call call = null;
	EndpointStateChange endpointStateChange = (EndpointStateChange) event;

	String theEndpointThatCameOnline = endpointStateChange.getEndpoint ().getResource ();

	//Person employee = Person.getPersonFromPJSIPHandle ("PJSIP/"+theEndpointThatCameOnline);
	HolaAccount employee = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (theEndpointThatCameOnline));
	if (employee != null)
	    {
		if (endpointStateChange.getEndpoint ().getState ().equals ("online"))
		    {
			log.debug ("Thread ID " + Thread.currentThread ().getId () + 
					    " EndpointStateChange processing: " + 
					    theEndpointThatCameOnline + " came online");
			
			call = CallDB.getCallFromANumber (Integer.toString (employee.getExtension ()));

			boolean PSTNHandleIsBeingUsedInCall;

			if (call != null)
			    {
				if ((call.getCaller ().equals (Integer.toString (employee.getExtension ()))) ||
				    (call.getCallee ().equals (Integer.toString (employee.getExtension ()))))
				    {
					call.setLastCSMEvent (CSMEvent.SIPEndpointOfActiveCallRegistered);
					PSTNHandleIsBeingUsedInCall = true;
				    }
				else
				    {
					PSTNHandleIsBeingUsedInCall = false;
				    }
			    }			
		    }
		else if (endpointStateChange.getEndpoint ().getState ().equals ("offline"))
		    {		
			log.debug ("Thread ID " + Thread.currentThread ().getId () + 
					    " EndpointStateChange processing: " + 
					    theEndpointThatCameOnline + " went offline");
		    }
	    }

	return call;
    }
    
    private void processChannelDtmfReceived (Event event)
    {
	// SUSPECT SUSPECT SUSPECT. 
	// WHat does Person.getpersonfromhandle do when numberCall is not an extension????
	log.debug ("Thread ID " + Thread.currentThread ().getId () + " ChannelDtmfReceived processing");
	ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;
	String numberCallIsFrom = channelDtmfReceived.getChannel ().getCaller ().getNumber ();	
	ActiveCall activeCall = ActiveCallsHelper.getByChannelID (channelDtmfReceived.getChannel ().getId ());

	if (activeCall == null)
	    {
		return; // nothing to do here
	    }
	// unfortunately, this wont work if this is an extension that was entered via DTMF
	// SUSPECT SUSPECT SUSPECT
	HolaAccount personCallIsFrom = AccountHandlesHelper.getHolaAccountFromHandle (numberCallIsFrom);
	if (personCallIsFrom != null)
	    {
		// Call from employee
		Call call = CallDB.getCallFromCallerNumber (Integer.toString (personCallIsFrom.getExtension ()));

		if (call != null)
		    {
			//System.out.println (Color.Cyan + "\tProcessing ChannelDtmfReceived: Digit" + 
			//		    channelDtmfReceived.getDigit () + Color.End);
			call.processChannelDtmfReceived (event);
		    }
	    }
	else
	    {
		Call call = CallDB.getCallFromCallerNumber (numberCallIsFrom); // Call from non-employee

		if (call != null)
		    {
			call.processChannelDtmfReceived (event);
		    }
	    }
    }

    private Call getCallContextFromChannelHangupRequest (Event event)
    {
	Call call = null;
	ChannelHangupRequest channelHangupRequest = (ChannelHangupRequest) event;
	String stateOfChannel = channelHangupRequest.getChannel ().getState ();
	switch (stateOfChannel)
	    {
	    case "Ringing":
		// In this case, the callee didn't want to take the call. Trigger an OutgoingChannelHungup event
		String callUUID = 
		    ActiveCallsHelper
		    .getUnfinishedCallUUIDFromCalleeID (channelHangupRequest.getChannel ().getCaller ().getNumber ());
		log.debug ("Will look for uuid " + callUUID);
		call = CallDB.getCallFromUUID (callUUID);
		//call.removeFromCalleeChannels (channelHangupRequest.getChannel ().getId ());
		if (call != null)
		    {
			call.setLastCSMEvent (CSMEvent.OutgoingChannelHungup);
			log.debug ("\n\n\nOutgoingChannelHungup (getCallContextFromChannelHangupRequest)\n\n\n" +
					    ThreadUtils.getThreadIDedStackTraceString () + "\n\n\n\n\n\n");
			ActiveCallsHelper
			    .removeUnfinishedCallByCalleeID (channelHangupRequest.getChannel ().getCaller ().getNumber ());
			makeCorrectTransitions (call, event); // Only this type of channelHangup should result in thisx
		    }
		else
		    {
			log.debug ("No call found for this UUID!");
		    }
		break;
	    case "Up":
		// Do nothing
		break;
	    default:
		// Do nothing
	    }
	return call; // placeholder
    }

    private Call getCallContextFromChannelDtmfReceived (Event event)
    {
	ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;
	return new Call (); // placeholder
    }

    private void makeCorrectTransitions (Call call, Event event)
    {
	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
			    " Call " + call.getUUID () + " is in CSMEvent." + call.getLastCSMEvent () + " state");
	switch (call.getLastCSMEvent ())
	    {
	    case ExtensionToExtensionCall: // New context
		log.debug ("[Thread_ID: {}] Processing ExtensionToExtensionCall for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.changeState (getStateForStateName (CSMState.WaitForIncomingSetup));
		call.processExtensionToExtensionCall (event);
		break;
	    case ExtensionToExternalCall: // New context
		log.debug ("[Thread_ID: {}] Processing ExtensionToExternalCall for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.changeState (getStateForStateName (CSMState.WaitForExternalNumberValidation));
		call.processExtensionToExternalCall (event);
		break;
	    case CalleeProcessing: // Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelAnswer));
		*/
		log.debug ("[Thread_ID: {}] Processing CalleeProcessing for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processCalleeProcessing (event);
		break;
	    case SIPEndpointOfActiveCallRegistered: // Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForSIPChannelSetup));
		*/
		log.debug ("[Thread_ID: {}] Processing SIPEndpointOfActiveCallRegistered for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processSIPEndpointOfActiveCallRegistered (event);
		break;
	    case OutgoingChannelRing: // Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForOutgoingChannelAnswer));
		*/
		log.debug ("[Thread_ID: {}] Processing OutgoingChannelRing for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processOutgoingChannelRing (event);
		break;
	    case IncomingChannelHungup:	// Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForIncomingHangupProcessing));
		*/
		log.debug ("[Thread_ID: {}] Processing IncomingChannelHungup for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processIncomingHangup (event);
		break;
	    case OutgoingChannelHungup:	// Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForOutgoingHangupProcessing));
		*/
		log.debug ("[Thread_ID: {}] Processing OutgoingChannelHungup for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processOutgoingHangup (event);
		break;
	    case FromPSTNForExistingCall: // Pre-existing context
		/*
		  The following line should not be necessary...
		  call.changeState (getStateForStateName (CSMState.WaitForHandoutPrep));
		*/
		log.debug ("[Thread_ID: {}] Processing FromPSTNForExistingCall for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.processFromPSTNForExistingCall (event);
		break;
	    case FromPSTNForNewCall: // New context
		log.debug ("[Thread_ID: {}] Processing FromPSTNForNewCall for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());
		call.changeState (getStateForStateName (CSMState.WaitForDTMF));
		call.processFromPSTNForNewCall (event);
		break;
	    case SIPLegHandInForActiveCallRequested:
		log.debug ("[Thread_ID: {}] Processing SIPLegHandInForActiveCallRequested for call {}", 
			   Thread.currentThread ().getId (), call.getUUID ());

		call.processSIPLegHandInForActiveCallRequested (event);
		break;
	    default:
		log.error ("Unknown state {} for call", call.getLastCSMEvent ());
		// Error event?
	    }
    }
}
