/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
/*
 * 09.05.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Hola! Call state.
 *
 */
package com.hola.serverSide.ariApplication.call;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.lang.InterruptedException;
import java.time.LocalTime;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.ChannelDtmfReceived;
import com.hola.serverSide.ariApplication.ari.Channel;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.Bridge;
import com.hola.serverSide.ariApplication.csm.CSMBaseState;
import com.hola.serverSide.ariApplication.csm.CSMIdleState;
import com.hola.serverSide.ariApplication.csm.CSMState;
import com.hola.serverSide.ariApplication.csm.CSMEvent;
import com.hola.serverSide.ariApplication.common.SessionTokenUtils;
import com.hola.serverSide.ariApplication.common.ThreadUtils;
import com.hola.serverSide.ariApplication.common.Color;
import com.hola.serverSide.ariApplication.policy.ActionPlan;
import com.hola.serverSide.ariApplication.charging.Charging;
import com.hola.serverSide.ariApplication.db.beans.ActiveCall;
import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.logging.CallEventLogger;

public class Call
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (Call.class.getName ());

    private          final  String                    UUID;
    private volatile        CSMBaseState              currentState; // The current state gets upcasted to the CSMBaseState
    private volatile        CSMEvent                  lastCSMEvent; // This is the last event that was processed for this call
    private volatile        String                    caller;
    private volatile        boolean                   callerIsExternal;
    private volatile        String                    callerDTMFReceivedSoFar;
    private volatile        LocalTime                 currentTimeAtCaller;
    //private volatile        Location                  callerLocation;
    private volatile        LinkedHashMap <String, Channel> callerChannels;
    private volatile        String                    callee;
    private volatile        boolean                   calleeIsExternal;
    private volatile        String                    calleeDTMFReceivedSoFar;
    private volatile        LocalTime                 currentTimeAtCallee;
    //private volatile        Location                  calleeLocation;
    private volatile        LegInfo                    calleeLegInfo;
    private volatile        String                    calleeQueryString;
    private volatile        LinkedHashMap <String, Channel> calleeChannels;
    private volatile        int                       numberOfDTMFTriesBeforeSuccess;
    private volatile        int                       duration; // seconds
    //private volatile        List <String>             executionTrail;
    private volatile        ActionPlan                actionPlan;
    //private volatile        Charging                  charging;
    private volatile        Bridge                    bridge;

    //private volatile static HashMap <String, Call>    callDB;

    public Call ()
    {
	this.UUID                = SessionTokenUtils.getARandomUUID ();
	this.currentState        = CSMIdleState.getInstance ();
	this.lastCSMEvent        = CSMEvent.UNKNOWN;
	this.duration            = 0;
	this.currentTimeAtCaller = LocalTime.now ();
	this.currentTimeAtCallee = LocalTime.now ();
	this.actionPlan          = new ActionPlan ();
    }

    public Call (String _caller, String _callee, CSMEvent _lastCSMEvent)
    {
	this.UUID                           = SessionTokenUtils.getARandomUUID ();
	this.currentState                   = CSMIdleState.getInstance ();
	this.lastCSMEvent                   = _lastCSMEvent;
	this.caller                         = _caller;
	this.callee                         = _callee;

	if (this.caller.length () != 4)
	    {
		this.callerIsExternal = true;
	    }
	if (this.callee.length ()!= 4)
	    {
		this.calleeIsExternal = true;
	    }

	this.duration                       = 0;
	this.callerChannels                 = new LinkedHashMap <String, Channel> ();
	this.calleeChannels                 = new LinkedHashMap <String, Channel> ();
	this.callerDTMFReceivedSoFar        = "";
	this.calleeDTMFReceivedSoFar        = "";
	this.numberOfDTMFTriesBeforeSuccess = 0;
	this.currentTimeAtCaller            = LocalTime.now ();
	this.currentTimeAtCallee            = LocalTime.now ();
	this.actionPlan                     = new ActionPlan ();

	CallDB.addACallIntoDB (this);
    }

    @Override
    public synchronized String toString ()
    {
	String callStr = this.UUID + "/[" + this.caller + "->" + this.callee + "]/CurrentState = [" + 
	    currentState.getStateName () + "]/[Last CSM Event = " + lastCSMEvent + "]";
	return callStr;
    }

    public synchronized void printObject ()
    {
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " UUID = " + this.UUID+ 
			    Color.End);
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " caller = " + this.caller+ 
			    Color.End);
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " callee = " + this.callee+ 
			    Color.End);
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " last CSMEvent = " + 
			    this.lastCSMEvent.getCallEventTypeString ()+ Color.End);
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " current state = " + 
			    this.currentState.getStateName () + Color.End);

	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " callerChannels size = " + 
			    this.callerChannels.size ()+ Color.End);

	Iterator callerChannelIterator = this.getCallerChannels ().entrySet ().iterator ();
	
	while (callerChannelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
		Channel callerChannel = (Channel) channelEntry.getValue ();
		
		System.out.println (Color.Yellow + "\t\tThread ID " + Thread.currentThread ().getId () + 
				    " Caller's channelID = " + callerChannel.getId ()+ Color.End);
	    }
	System.out.println (Color.Yellow + "\tThread ID " + Thread.currentThread ().getId () + " calleeChannels size = " + 
			    this.calleeChannels.size ()+ Color.End);

	Iterator calleeChannelIterator = this.getCalleeChannels ().entrySet ().iterator ();
	
	while (calleeChannelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
		Channel calleeChannel = (Channel) channelEntry.getValue ();
		
		System.out.println (Color.Yellow + "\t\tThread ID " + Thread.currentThread ().getId () + 
				    " Callee's channelID = " + calleeChannel.getId () + Color.End);
		
	    }
    }

    public synchronized void logObject ()
    {
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] UUID = " + this.UUID);
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] caller = " + this.caller);
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] callee = " + this.callee);
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] last CSMEvent = " + 
		  this.lastCSMEvent.getCallEventTypeString ());
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] callerChannels size = " + 
		  this.callerChannels.size ());
	Iterator callerChannelIterator = this.getCallerChannels ().entrySet ().iterator ();
	
	while (callerChannelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
		Channel callerChannel = (Channel) channelEntry.getValue ();
		
		log.debug ("\tThread ID " + Thread.currentThread ().getId () + 
			   ": Caller's channelID = " + callerChannel.getId ());
	    }
	log.debug("[Thread_ID:  " + Thread.currentThread ().getId () + "] calleeChannels size = " + 
		  this.calleeChannels.size ());
	Iterator calleeChannelIterator = this.getCalleeChannels ().entrySet ().iterator ();
	
	while (calleeChannelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
		Channel calleeChannel = (Channel) channelEntry.getValue ();
		
		log.debug ("\tThread ID " + Thread.currentThread ().getId () + 
			    ": Callee's channelID = " + calleeChannel.getId ());
		
	    }
    }

    
    public synchronized void setLastCSMEvent (CSMEvent _lastCSMEvent)
    {
	this.lastCSMEvent = _lastCSMEvent;
	// updateAnExistingCall (this);
    }

    
    // public static synchronized void // updateAnExistingCall (Call call)
    // {
    // 	if (callDB.containsKey (call.getUUID ()))
    // 	    {
    // 		callDB.put (call.getUUID (), call);
    // 		log.debug ("[Thread_ID: {}] Call {} updated", Thread.currentThread ().getId (), call.getUUID ());
    // 	    }
    // 	else
    // 	    {
    // 		log.error ("Call DB does not have the call with UUID {}", call.getUUID ());
    // 	    }
    // }
    
    public synchronized void setCaller (String _caller)
    {
	this.caller = _caller;
	// updateAnExistingCall (this);
    }

    public synchronized void setCallerIsExternal (boolean _callerIsExternal)
    {
	this.callerIsExternal = _callerIsExternal;
    }

    public synchronized void setCallee (String _callee)
    {
	this.callee = _callee;
	// updateAnExistingCall (this);
    }

    public synchronized void setCalleeIsExternal (boolean _calleeIsExternal)
    {
	this.calleeIsExternal = _calleeIsExternal;
    }

    public synchronized void setDuration (int _duration)
    {
	this.duration = _duration;
	// updateAnExistingCall (this);
    }
    
    public synchronized void setBridge (Bridge _bridge)
    {
	this.bridge = _bridge;
	// updateAnExistingCall (this);
    }
    
    public synchronized void setCurrentTimeAtCaller (LocalTime _currentTimeAtCaller)
    {
	this.currentTimeAtCaller = _currentTimeAtCaller;
    }

    // public synchronized void setCallerLocation (Location _callerLocation)
    // {
    // 	this.callerLocation = _callerLocation;
    // }

    public synchronized void setCurrentTimeAtCallee (LocalTime _currentTimeAtCallee)
    {
	this.currentTimeAtCallee = _currentTimeAtCallee;
    }

    // public synchronized void setCalleeLocation (Location _calleeLocation)
    // {
    // 	this.calleeLocation = _calleeLocation;
    // }

    public synchronized void setCalleeLegInfo (LegInfo _legInfo)
    {
	this.calleeLegInfo = _legInfo;
    }

    public synchronized void setCalleeQueryString (String _queryString)
    {
	this.calleeQueryString = _queryString;
    }

    public synchronized void incNumberOfDTMFTriesBeforeSuccess ()
    {
	this.numberOfDTMFTriesBeforeSuccess ++;
	// updateAnExistingCall (this);
    }

    public synchronized void resetNumberOfDTMFTriesBeforeSuccess ()
    {
	this.numberOfDTMFTriesBeforeSuccess = 0;
	// updateAnExistingCall (this);
    }

    public synchronized int getNumberOfDTMFTriesBeforeSuccess ()
    {
	return this.numberOfDTMFTriesBeforeSuccess;
    }

    public synchronized ActionPlan getActionPlan ()
    {
	return this.actionPlan;
    }


    public synchronized boolean callerIsNotAnEmployee ()
    {
	return this.callerIsExternal;
    }

    public synchronized boolean calleeIsNotAnEmployee ()
    {
	return this.calleeIsExternal;
    }

    public synchronized String getCallerDTMFReceived ()
    {
	return this.callerDTMFReceivedSoFar;
    }

    public synchronized String getCalleeDTMFReceived ()
    {
	return this.calleeDTMFReceivedSoFar;
    }

    public synchronized void appendToCallerDTMFReceived (String digit)
    {
	this.callerDTMFReceivedSoFar += digit;
	// updateAnExistingCall (this);
    }

    public synchronized void appendToCalleeDTMFReceived (String digit)
    {
	this.calleeDTMFReceivedSoFar += digit;
	// updateAnExistingCall (this);
    }

    public synchronized void resetCallerDTMFReceivedSoFar ()
    {
	this.callerDTMFReceivedSoFar = "";
	// updateAnExistingCall (this);
    }

    public synchronized void resetCalleeDTMFReceivedSoFar ()
    {
	this.calleeDTMFReceivedSoFar = "";
	// updateAnExistingCall (this);
    }

    public synchronized String getUUID ()
    {
	return this.UUID;
    }

    public synchronized CSMEvent getLastCSMEvent ()
    {
	return this.lastCSMEvent;
    }

    public synchronized String getCaller ()
    {
	return this.caller;
    }

    public synchronized boolean getCallerIsExternal ()
    {
	return this.callerIsExternal;
    }

    public synchronized boolean getCalleeIsExternal ()
    {
	return this.calleeIsExternal;
    }

    public synchronized String getCallee ()
    {
	return this.callee;
    }
    
    public synchronized int getDuration ()
    {
	return this.duration;
    }

    public synchronized Bridge getBridge ()
    {
	return this.bridge;
    }

    public synchronized LocalTime getCurrentTimeAtCaller ()
    {
	return this.currentTimeAtCaller;
    }

    // public synchronized Location getCallerLocation ()
    // {
    // 	return this.callerLocation;
    // }

    public synchronized LocalTime getCurrentTimeAtCallee ()
    {
	return this.currentTimeAtCallee;
    }

    // public synchronized Location getCalleeLocation ()
    // {
    // 	return this.calleeLocation;
    // }

    public synchronized LegInfo getCalleeLegInfo ()
    {
	return this.calleeLegInfo;
    }

    public synchronized String getCalleeQueryString ()
    {
	return this.calleeQueryString;
    }


    public synchronized void changeState (CSMBaseState newState)
    {
	log.debug ("[Thread_ID: {}] Call {}/Last CSMEvent {}: state change from {} to {}", ThreadUtils.getThreadIDString (),
		   this.getUUID (), this.getLastCSMEvent (), this.getCurrentStateName (), newState.getStateName ());
	CallEventLogger.callLog (this, "State change from " + this.getCurrentStateName () + 
				 " to " + newState.getStateName ());
	this.currentState = newState;
	// updateAnExistingCall (this);
    }
    
    public synchronized CSMBaseState getCurrentState ()
    {
	// Call the current state's processing functions
	return this.currentState;
    }

    public synchronized String getCurrentStateName ()
    {
	// Call the current state's processing functions
	return this.currentState.getStateName ();
    }

    public synchronized LinkedHashMap <String, Channel> getCallerChannels ()
    {
	return this.callerChannels;
    }

    public synchronized LinkedHashMap <String, Channel> getCalleeChannels ()
    {
	return this.calleeChannels;
    }

    public synchronized String getFirstCallerChannelID ()
    {
	if (this.callerChannels.size () != 0)
	    {
		//System.out.println ("Non-zero caller channel ID size");
		return (String) this.getCallerChannels ().keySet ().toArray () [0];
	    }
	else
	    {
		//System.out.println ("Zero caller channel ID size");
		return "";
	    }
    }

    public synchronized String getFirstCalleeChannelID ()
    {
	if (this.calleeChannels.size () != 0)
	    {
		//System.out.println ("Non-zero callee channel ID size");
		return (String) this.getCalleeChannels ().keySet ().toArray () [0];
	    }
	else
	    {
		//System.out.println ("Zero callee channel ID size");
		//printObject ();
		return "";
	    }
    }
    
    public synchronized void addToCallerChannels (Channel channel)
    {
	//log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Adding channelID " 
	//+ channel.getId () + " to caller's channel for call " + this.getUUID ());
	log.debug ("[Thread_ID: {}]: Adding channelID {} to caller's channel for call {}\n{}", 
		   Thread.currentThread ().getId (), channel.getId (), this.getUUID (), 
		   ThreadUtils.getThreadIDedStackTraceString ());
	CallEventLogger.callLog (this, "Added caller channel " + channel.getId ());
	this.callerChannels.put (channel.getId (), channel);
	// updateAnExistingCall (this);
    }

    public synchronized boolean isCallerChannel (String channelID)
    {
	if (this.callerChannels.get (channelID) != null)
	    {
		return true;
	    }
	return false;
    }

    public synchronized void addToCalleeChannels (Channel channel)
    {
	//log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Adding channelID " + 
	//		    channel.getId () + " to callee's channel for call " + this.getUUID ());
	log.debug ("[Thread_ID: {}]: Adding channelID {} to callee's channel for call {}\n{}", 
		   Thread.currentThread ().getId (), channel.getId (), this.getUUID (),
		   ThreadUtils.getThreadIDedStackTraceString ());
	CallEventLogger.callLog (this, "Added callee channel " + channel.getId ());
	this.calleeChannels.put (channel.getId (), channel);
	// updateAnExistingCall (this);
    }

    public synchronized boolean isCalleeChannel (String channelID)
    {
	if (this.calleeChannels.get (channelID) != null)
	    {
		return true;
	    }
	return false;
    }

    public synchronized void removeFromCallerChannels (String channelID)
    {
	log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Caller channel with channelID = " + channelID + 
		   " removed for call " + getUUID () + "/" + getLastCSMEvent ());
	log.debug (ThreadUtils.getThreadIDedStackTraceString ());
	
	log.debug ("[Thread_ID: {}]: Caller channelID {} removed from caller channels for call {}", 
		   Thread.currentThread ().getId (), channelID, this.getUUID ());
	CallEventLogger.callLog (this, "Removed caller channel " + channelID);
	this.callerChannels.remove (channelID);
	// updateAnExistingCall (this);
    }

    public synchronized void removeFromCalleeChannels (String channelID)
    {
	 log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Callee channel with channelID = " + channelID + 
		    " removed for call " + getUUID () + "/" + getLastCSMEvent ());
	 log.debug (ThreadUtils.getThreadIDedStackTraceString ());

	 log.debug ("[Thread_ID: {}]: Callee channelID {} removed from callee channels for call {}", 
		    Thread.currentThread ().getId (), channelID, this.getUUID ());
	CallEventLogger.callLog (this, "Removed callee channel " + channelID);
	this.calleeChannels.remove (channelID);
	// updateAnExistingCall (this);
    }

    public synchronized void removeFromCalleeChannelByCalleeNumber (String number)
    {
	Iterator channelIterator = this.getCalleeChannels ().entrySet ().iterator ();
	boolean updated = false;

	while (channelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) channelIterator.next ();
		Channel channel = (Channel) channelEntry.getValue ();
		if (channel.getCaller ().getNumber ().equals (number))
		    {
			channelIterator.remove ();
			updated = true;
		    }
		
	    }
	// update the callDB
	if (updated)
	    {
		// updateAnExistingCall (this);
	    }
    }


    public synchronized void removeFromCallerChannelByCallerNumber (String number)
    {
	Iterator channelIterator = this.getCallerChannels ().entrySet ().iterator ();
	boolean updated = false;
	while (channelIterator.hasNext ())
	    {
		Map.Entry channelEntry = (Map.Entry) channelIterator.next ();
		Channel channel = (Channel) channelEntry.getValue ();
		if (channel.getCaller ().getNumber ().equals (number))
		    {
			channelIterator.remove ();
			updated = true;
		    }
		
	    }
	// update the callDB
	if (updated)
	    {
		// updateAnExistingCall (this);
	    }
    }

    public synchronized CSMEvent handleIncorrectDTMF (Event event)
    {
	ChannelDtmfReceived channelDtmfReceived = (ChannelDtmfReceived) event;

	this.incNumberOfDTMFTriesBeforeSuccess ();
	String dtmf = getCallerDTMFReceived ();
	this.resetCallerDTMFReceivedSoFar ();

	System.out.println (Color.BrightGreen + "DTMF so far: " + dtmf + Color.End);

	if (this.getNumberOfDTMFTriesBeforeSuccess () > 3)
	    {
		/*
		  First, remove the call from the activeCalls table
		 */
		System.out.println (Color.Red + "Removing caller entering DTMF..." + Color.End);
		ActiveCallsHelper.removeUnfinishedCallByCallerChannel (channelDtmfReceived.getChannel ().getId ());
		this.removeFromCalleeChannels (channelDtmfReceived.getChannel ().getId ());
		//CallDB.

		// This feature seems to annoy a lot of people
		// ClientResponse playGoodbyeResponse = ChannelsARIAPI.play (channelDtmfReceived.getChannel ().getId (),
		// 							  "?media=sound:goodbye");
		// if (playGoodbyeResponse.getStatus () == 404)
		//     {
		// 	log.error ("Channel {} not found", channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.Error;
		//     }
		// if (playGoodbyeResponse.getStatus () == 409)
		//     {
		// 	log.error ("Channel {} not in a Stasis application", 
		// 		   channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.Error;

		//     }
		try
		    {
			Thread.sleep (1500);
		    }
		catch (InterruptedException e)
		    {
			e.printStackTrace ();
		    }

		return CSMEvent.Error;
	    }
	else // Send back to get fresh DTMF
	    {
		System.out.println (Color.Yellow + "Get fresh DTMF..." + Color.End);

		// 05/08/2016: These messages seem to annoy people
		// ClientResponse playMessageResponse = ChannelsARIAPI.play (channelDtmfReceived.getChannel ().getId (),
		// 							  "?media=sound:wrong-try-again-smarty");
		// if (playMessageResponse.getStatus () == 404)
		//     {
		// 	log.error ("Channel {} not found", channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.Error;
		//     }
		// if (playMessageResponse.getStatus () == 409)
		//     {
		// 	log.error ("Channel {} not in a Stasis application", 
		// 		   channelDtmfReceived.getChannel ().getId ());
		// 	return CSMEvent.Error;
		//     }
		return CSMEvent.FromPSTNForNewCall;
	    }
	// Don't do anything else yet--just want for more DTMF

    }

    public synchronized void processIncomingHangup (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processIncomingHangup (this, event);
    }

    public synchronized void processOutgoingHangup (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingHangup (this, event);
    }

    public synchronized void processExtensionToExtensionCall (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processExtensionToExtensionCall (this, event);
    }

    public synchronized void processExtensionToExternalCall (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processExtensionToExternalCall (this, event);
    }

    public synchronized void processFromPSTNForNewCall (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processFromPSTNForNewCall (this, event);
    }

    public synchronized void processFromPSTNForExistingCall (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processFromPSTNForExistingCall (this, event);
    }

    public synchronized void processSIPLegHandInForActiveCallRequested (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processSIPLegHandInForActiveCallRequested (this, event);
    }

    public synchronized void processCalleeProcessing (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processCalleeProcessing (this, event);
    }

    public synchronized void processSIPEndpointOfActiveCallRegistered (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processSIPEndpointOfActiveCallRegistered (this, event);
    }

    public synchronized void processDTMFRecognizedAsEmployee (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processDTMFRecognizedAsEmployee (this, event);
    }

    public synchronized void processDTMFRecognizedAsExternalNumber (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processDTMFRecognizedAsExternalNumber (this, event);
    }

    public synchronized void processError (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processError (this, event);
    }

    public synchronized void processCleanUp (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processCleanUp (this, event);
    }

    public synchronized void processOutgoingMappingNotFound (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingMappingNotFound (this, event);
    }

    public synchronized void processSuccess (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processSuccess (this, event);
    }

    public synchronized void processCallSetupFailureIncomingChannelHungup (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processCallSetupFailureIncomingChannelHungup (this, event);
    }

    public synchronized void processCallSetupFailureOutgoingChannelHungup (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processCallSetupFailureOutgoingChannelHungup (this, event);
    }

    public synchronized void processOutgoingChannelNotAvailable (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingChannelNotAvailable (this, event);
    }

    public synchronized void processOutgoingChannelAvailable (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingChannelAvailable (this, event);
    }

    public synchronized void processOutgoingMappingFound (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingMappingFound (this, event);
    }

    public synchronized void processOutgoingChannelRing (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processOutgoingChannelRing (this, event);
    }

    public synchronized void processChannelDtmfReceived (Event event)
    {
	log.info ("[Thread_ID: {}] Call {}/current state = {} processing event {}", Thread.currentThread ().getId (), 
		  this.getUUID (), this.currentState.getStateName (), event.getType ());
	currentState.processChannelDtmfReceived (this, event);
    }
}
