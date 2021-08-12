/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.call;

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
import com.hola.serverSide.ariApplication.common.ResultCode;
import com.hola.serverSide.ariApplication.common.Color;
import com.hola.serverSide.ariApplication.csm.CSMEvent;
import com.hola.serverSide.ariApplication.ari.ChannelsARIAPI;
import com.hola.serverSide.ariApplication.ari.Channel;
import com.hola.serverSide.ariApplication.policy.Policy;
import com.hola.serverSide.ariApplication.policy.Action;
import com.hola.serverSide.ariApplication.policy.ActionGroup;
import com.hola.serverSide.ariApplication.policy.RejectAction;

import com.hola.serverSide.ariApplication.db.ActiveCallsHelper;
import com.hola.serverSide.ariApplication.db.HolaAccountsHelper;
import com.hola.serverSide.ariApplication.db.beans.ActiveCall;
import com.hola.serverSide.ariApplication.db.beans.HolaAccount;

public class CalleeUtils
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CalleeUtils.class.getName ());

    public static Result originateCalleeChannel ()
    {
	return new Result (ResultCode.Success, "Successful", CSMEvent.UNKNOWN);
    }

    public static Result originateChannelToInternalCallee ()
    {
	return new Result (ResultCode.Success, "Successful", CSMEvent.UNKNOWN);
    }

    public static Result originateChannelToExternalCallee ()
    {
	return new Result (ResultCode.Success, "Successful", CSMEvent.UNKNOWN);
    }

    public static String getQueryStringToPlaceCall (Call call)
    {
	String query=null;
	LegInfo calleeLeg = call.getCalleeLegInfo ();
	String callerID = null;
	ActiveCall activeCall = ActiveCallsHelper.getActiveCallByUUID (call.getUUID ());
	HolaAccount 
	    callersHolaAccount = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (activeCall.getCallerID ()));
	HolaAccount calleesHolaAccount = calleeLeg.getHolaAccount ();
	boolean calleeQueryStringIsPSTNE164Handle = false;

	if (call.getCalleeQueryString ().substring (6, 7).equals ("+"))
	    {
		calleeQueryStringIsPSTNE164Handle = true;
	    }

	System.out.println ("Callee is " + call.getCallee () + " and calleeQueryString = " + call.getCalleeQueryString ());
	//if (call.getCallee ().substring (0,1).equals ("+"))
	//if (call.getCalleeQueryString ().substring (6, 7).equals ("+"))
	if (calleeQueryStringIsPSTNE164Handle)
	    {
		System.out.println (Color.BrightGreen + "Call is external" + Color.End);
		// Try this for voice obfuscation features
		int returnCode = 
		    ActiveCallsHelper.updateActiveCallWithCalleeHandle (call.getUUID (), 
									call.getCalleeQueryString ().substring (6, 18));
		// 
		call.setCalleeIsExternal (true);
	    }
	else
	    {
		System.out.println (Color.Red + "Call is NOT external" + Color.End);
	    }
	
	try
	    {
		//System.out.println ("Call " + call.getUUID ());
		query = "?app=psa&endpoint=";

		if (callersHolaAccount != null)
		    {
			if (!call.getCalleeIsExternal ())
			    {
				callerID = URLEncoder.encode (callersHolaAccount.getFirstname () + " " + 
							      callersHolaAccount.getLastname (), "UTF-8");
			    }
			else
			    {
				// Info will be packed in body param
			    }
		    }
		else // Caller not an Hola! user. E.g., a non-Hola! user calling in and getting redirected to an Hola! user's 
		    //   other PSTN handle
		    {
			callerID = URLEncoder.encode (activeCall.getCallerID (), "UTF-8"); // Same as caller handle
		    }
		
		if (calleesHolaAccount != null)
		    {
			//callerID = calleeLeg.getHolaAccount ().getFirstname ();
			System.out.println (Color.Red + "\n\n\n\n\n" + calleeLeg.getHandle () + "\n\n\n\n" + Color.End);
			query = query + URLEncoder.encode (calleeLeg.getHandle (), "UTF-8");
			//query = query + "&body=" + URLEncoder.encode ("{\"variables\" : { \"CALLERID(name)\": \"Alice\" }}", 
			// "UTF-8");
		    }
		else // Callee not an Hola! user
		    {
			query = query + URLEncoder.encode (call.getCalleeQueryString (), "UTF-8");
			//query = query + "&body=" + 
			// URLEncoder.encode ("{\"variables\" : { \"CALLERID(name)\": \"Alice\", 
			// \"CALLERID(num\": \"7812096264\" }}", "UTF-8");
			//callerID = "External";
		    }
		query = query + "&appArgs=dialed";

		if (!call.getCalleeIsExternal ())
		    {
			query = query + "&callerId=" + callerID; // Correct
		    }
		else
		    {
			query = query + "&callerId=" + URLEncoder.encode (callersHolaAccount.getFirstname () + " " + 
									  callersHolaAccount.getLastname () + 
									  " <7812096264>", "UTF-8");
			//query = query + "&callerId=7812096264"; // worked
		    }
	    }
	catch (UnsupportedEncodingException e)
	    {
		log.error (e.toString ());
		e.printStackTrace ();
	    }
	finally
	    {
		System.out.println (Color.DarkGreen + "Calling using query " + query + Color.End);
		return query;
	    }
    }

    public static Result originateCalleeChannelWithQueryStringAndBody (Call call, String query, String body)
    {
	Result result = null;
	ActiveCall activeCall = ActiveCallsHelper.getActiveCallByUUID (call.getUUID ());
	String bodyParam = "";
	HolaAccount callersHolaAccount = HolaAccountsHelper.getHolaAccountByExtension (Integer.parseInt (activeCall.getCallerID ()));
	HolaAccount calleesHolaAccount = call.getCalleeLegInfo ().getHolaAccount ();

	// if (call.getCalleeIsExternal ())
	//     {
	// 	bodyParam = "{\"variables\" : { \"CALLERID(name)\": \"" + callersHolaAccount.getFirstname () + " " + 
	// 	    callersHolaAccount.getLastname () + "\", \"CALLERID(num)\": \"7812096264\" }}";
	// 	System.out.println ("Will pass body param " + bodyParam);
	//     }

	if (query != null)
	    {
		log.debug ("\n\n\n\n\n\n\n\n\nUsing query == " + query + "\n\n\n\n\n\n\n\n");
		// Tried packing the body param per 
		// https://wiki.asterisk.org/wiki/display/AST/Asterisk+13+Channels+REST+API#Asterisk13ChannelsRESTAPI-originate
		// And that did not work. See a solution that partially works in the getQueryStringToPlaceCall method
		// above.
		ClientResponse outgoingChannelCreateResponse = ChannelsARIAPI.originate (query, "");
		if (outgoingChannelCreateResponse.getStatus () == 400)
		    {
			log.error ("Invalid parameters for originating a channel; transitioning" + 
			   " to the CleanUp (or Error?) state");
			return new Result (ResultCode.Error, "Outgoing mapping was not found/400", 
					   CSMEvent.OutgoingMappingNotFound);
		    }
		else if (outgoingChannelCreateResponse.getStatus () == 500)
		    {
			log.error ("Likely that the endpoint is not logged in. Should look for another " +
				   "handle for this user. What is plan B per the action plan?");
			log.error ("The content back from the server is {}", 
				   outgoingChannelCreateResponse.getEntity (String.class));
			return new Result (ResultCode.Error, "Outgoing mapping was not found/500", 
					   CSMEvent.OutgoingMappingNotFound);
		    }

		String output = outgoingChannelCreateResponse.getEntity (String.class);
		ObjectMapper mapper = new ObjectMapper ();
		
		try
		    {
			Channel outgoing = mapper.readValue (output, Channel.class);
			
			/*
			 * outgoingChannelCreateResponse actually contains the channel object that asterisk created
			 * for the outgoing/callee processing. You can use this information to "map" the caller
			 * to the callee and its outgoing channel.
			 */
			
			log.debug ("Thread ID " + Thread.currentThread ().getId () +
					    " In CSMWaitForOutgoingChannelRequestState, adding new outgiong channel " + 
					    outgoing.getId () + " to call");
			call.addToCalleeChannels (outgoing);
		    }
		catch (IOException e)
		    {
			e.printStackTrace ();
		    }
		log.debug ("Returning success");
		return new Result (ResultCode.Success, "Outgoing channel available", CSMEvent.OutgoingChannelAvailable);
	    }
	else
	    {
		log.debug ("Returning error :-(");
		return new Result (ResultCode.Error, "Outgoing channel not available", CSMEvent.OutgoingChannelNotAvailable); 
		// Different from OutgoingMappingNotFound!
	    }
    }

    public static boolean actionGroupContainsReject (ActionGroup actionGroup)
    {
	for (Action action: actionGroup.getActions ())
	    {
		if (action instanceof RejectAction)
		    {
			return true;
		    }
	    }
	return false;
    }



}
