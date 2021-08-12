package com.hola.serverSide.ariApplication.call;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * Hola specific
 */
import com.hola.serverSide.ariApplication.ari.Channel;
import com.hola.serverSide.ariApplication.common.ThreadUtils;
import com.hola.serverSide.ariApplication.logging.CallEventLogger;

public class CallDB
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CallDB.class.getName ());

    // // Eager initialization
    // private static final CallDB instance = new CallDB ();

    private volatile static HashMap <String, Call> callDB = new HashMap <String, Call> ();

    // private CallDB ()
    // {
    // 	callDB = new HashMap <String, Call> ();
    // }

    // public static CallDB getInstance ()
    // {
    // 	return instance;
    // }

    public synchronized static void addACallIntoDB (Call call)
    {
	callDB.put (call.getUUID (), call);
	CallEventLogger.callLog (call, "Call@" + Integer.toHexString(System.identityHashCode(call)) + " creation. WARNING! Call object hashIDs are not unique (only the UUIDs are unique)!");
	log.debug ("[Thread_ID: {}] CallDB size is now {}", Thread.currentThread ().getId (), callDB.size ());
    }

    public synchronized static void removeACallFromDB (String UUID)
    {
	CallEventLogger.callLog (callDB.get (UUID), "Call removal"); // SUSPECT SUSPECT SUSPECT
	callDB.remove (UUID);
	log.debug ("[Thread_ID: {}] CallDB size is now {}", Thread.currentThread ().getId (), callDB.size ());
    }

    public synchronized static Call getCallFromUUID (String UUID)
    {
	return callDB.get (UUID);
    }
    
    public synchronized static Call getCallFromCalleesChannelID (String channelID)
    {
	Call aCall = null;
	// log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Lookup callees channel " + 
	// 		    channelID + " in call db which is of size " + callDB.size ());

	Iterator callIterator = callDB.entrySet ().iterator ();
	log.debug (ThreadUtils.getThreadIDedStackTraceString ());

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		// if (call.getCalleeChannels () == null)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels is null!");
		//     }
		// else if (call.getCalleeChannels ().size () == 0)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels size is zero in getCallFromCalleesChannelID/" +
		// 			    call.getCurrentStateName ());
		//     }

		Iterator calleeChannelIterator = call.getCalleeChannels ().entrySet ().iterator ();

		while (calleeChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
			Channel calleeChannel = (Channel) channelEntry.getValue ();

			if (calleeChannel.getId ().equals (channelID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with callee's channelID = " + channelID);
				aCall = call;
			    }
		    }
	    }
	return aCall;
    }

    public synchronized static Call getCallFromCallersChannelID (String channelID)
    {
	Call aCall = null;
	// log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Lookup callers channel " + 
	// 		    channelID + " in call db which is of size " + callDB.size ());

	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		// if (call.getCallerChannels () == null)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Caller channels is null!");
		//     }
		// else if (call.getCallerChannels ().size () == 0)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Caller channels size is zero in getCallFromCallersChannelID/" +
		// 			    call.getCurrentStateName ());
		//     }

		Iterator callerChannelIterator = call.getCallerChannels ().entrySet ().iterator ();

		while (callerChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
			Channel callerChannel = (Channel) channelEntry.getValue ();

			if (callerChannel.getId ().equals (channelID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with caller's channelID = " + channelID);
				aCall = call;
			    }
		    }
	    }
	return aCall;
    }

    public synchronized static Call getCallFromAChannelID (String channelID)
    {
	Call aCall = null;
	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		Iterator callerChannelIterator = call.getCallerChannels ().entrySet ().iterator ();

		while (callerChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
			Channel callerChannel = (Channel) channelEntry.getValue ();

			if (callerChannel.getId ().equals (channelID))
			    {
				aCall = call;
			    }
		    }

		Iterator calleeChannelIterator = call.getCalleeChannels ().entrySet ().iterator ();

		while (calleeChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
			Channel calleeChannel = (Channel) channelEntry.getValue ();

			if (calleeChannel.getId ().equals (channelID))
			    {
				aCall = call;
			    }
		    }
	    }

	return aCall;
	
    }
    public synchronized static Call getActiveCallFromCallerNumberAndCalleeNumber (String callerNumber, String calleeNumber)
    {
	Call aCall = null;
	log.debug ("Thread ID " + Thread.currentThread ().getId () + "Lookup callerNumber " + callerNumber +
			    " and calleeNumber " + calleeNumber + " in call db which is of size " + callDB.size ());

	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();
		//log.debug ("Call {}", call.getUUID ());
		if ((call.getCaller ().equals (callerNumber)) && 
		    (call.getCallee ().equals (calleeNumber)) &&
		    (call.getCurrentStateName ().equals ("ActiveCallState")))
		    {
			aCall = call;
		    }
	    }
	return aCall;
    }

    public synchronized static boolean channelIDIsCaller (String channelID)
    {
	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		// if (call.getCallerChannels () == null)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Caller channels is null!");
		//     }
		// else if (call.getCallerChannels ().size () == 0)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Caller channels size is zero in channelIDIsCaller/"+
		// 			    call.getCurrentStateName ());
		//     }

		Iterator callerChannelIterator = call.getCallerChannels ().entrySet ().iterator ();

		while (callerChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
			Channel callerChannel = (Channel) channelEntry.getValue ();

			if (callerChannel.getId ().equals (channelID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with caller's channelID = " + channelID);
				return true;
			    }
		    }
	    }
	return false;
    }

    public synchronized static boolean channelIDIsCallee (String channelID)
    {
	Iterator callIterator = callDB.entrySet ().iterator ();
	// log.debug (ThreadUtils.getThreadIDedStackTraceString ());

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		// if (call.getCalleeChannels () == null)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels is null!");
		//     }
		// else if (call.getCalleeChannels ().size () == 0)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels size is zero in channelIDIsCallee/" +
		// 			    call.getCurrentStateName ());
		//     }

		Iterator calleeChannelIterator = call.getCalleeChannels ().entrySet ().iterator ();

		while (calleeChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
			Channel calleeChannel = (Channel) channelEntry.getValue ();

			if (calleeChannel.getId ().equals (channelID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with callee's channelID = " + channelID);
				return true;
			    }
		    }
	    }
	return false;
    }

    public synchronized static Call getCallFromCallerNumber (String number)
    {
	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		if (call.getCaller ().equals (number))
		    {
			return call;
		    }		
	    }
	return null;
    }
    
    public synchronized static Call getCallFromCalleeNumber (String number)
    {
	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		if (call.getCallee ().equals (number))
		    {
			return call;
		    }		
	    }
	return null;
    }

    public synchronized static Call getCallFromANumber (String number)
    {
	Iterator callIterator = callDB.entrySet ().iterator ();

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		if ((call.getCallee ().equals (number)) ||
		    (call.getCaller ().equals (number)))
		    {
			return call;
		    }		
	    }
	return null;
    }

    public synchronized static Channel getCallerChannelFromCallerID (String callerID)
    {// SUSPECT method. Check again
	Iterator callIterator = callDB.entrySet ().iterator ();

	log.debug ("Looking for {}; stacktrace: {}", callerID, ThreadUtils.getThreadIDedStackTraceString ());
	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		if (call.getCallerChannels () == null)
		    {
			log.debug ("Thread ID " + Thread.currentThread ().getId () + ": Callee channels is null!");
		    }
		else if (call.getCallerChannels ().size () == 0)
		    {
			log.debug ("Thread ID " + Thread.currentThread ().getId () + 
			 		    ": Callee channels size is zero in getCallerChannelFromCallerID/" +
			 		    call.getCurrentStateName ());
		    }

		Iterator callerChannelIterator = call.getCallerChannels ().entrySet ().iterator ();

		while (callerChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) callerChannelIterator.next ();
			Channel callerChannel = (Channel) channelEntry.getValue ();

			if (callerChannel.getCaller ().getNumber ().equals (callerID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with callee's channelID = " + callerChannel.getId ());
				return callerChannel;
			    }
		    }
	    }


	// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
	// 		    ": Returning null from getCalleeChannelFromCalleeID while looking for calleeID " + 
	// 		    callerID);
	return null;
    }

    public synchronized static Channel getCalleeChannelFromCalleeID (String calleeID)
    {// SUSPECT method. Check again
	Iterator callIterator = callDB.entrySet ().iterator ();
	log.debug (ThreadUtils.getThreadIDedStackTraceString ());

	while (callIterator.hasNext ())
	    {
		Map.Entry callEntry = (Map.Entry) callIterator.next ();
		Call call = (Call) callEntry.getValue ();

		// if (call.getCalleeChannels () == null)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels is null!");
		//     }
		// else if (call.getCalleeChannels ().size () == 0)
		//     {
		// 	log.debug ("Thread ID " + Thread.currentThread ().getId () + 
		// 			    ": Callee channels size is zero in getCalleeChannelFromCalleeID/" +
		// 			    call.getCurrentStateName ());
		//     }

		Iterator calleeChannelIterator = call.getCalleeChannels ().entrySet ().iterator ();

		while (calleeChannelIterator.hasNext ())
		    {
			Map.Entry channelEntry = (Map.Entry) calleeChannelIterator.next ();
			Channel calleeChannel = (Channel) channelEntry.getValue ();

			if (calleeChannel.getCaller ().getNumber ().equals (calleeID))
			    {
				// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				// 		    ": Returning call with callee's channelID = " + calleeChannel.getId ());
				return calleeChannel;
			    }
		    }
	    }

	// log.debug ("Thread ID " + Thread.currentThread ().getId () + 
	// 		    ": Returning null from getCalleeChannelFromCalleeID while looking for calleeID " + 
	// 		    calleeID);
	return null;
    }
}
