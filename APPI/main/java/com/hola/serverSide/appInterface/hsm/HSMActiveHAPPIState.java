/*
 * 12.29.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The HAPPI State Machine: ActiveHAPPI
 *
 * Singleton for ActiveHAPPI state of the HAPPI State Machine
 */
package com.hola.serverSide.appInterface.hsm;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific imports
 */
import com.hola.serverSide.appInterface.wSServer.APPIEventProcessorRunnable;
import com.hola.serverSide.appInterface.peer.APPIPeerState;
import com.hola.serverSide.appInterface.appi.APPIEvent;
import com.hola.serverSide.appInterface.appi.APPIEventType;
import com.hola.serverSide.appInterface.appi.InfoRequest;
import com.hola.serverSide.appInterface.appi.InfoResponse;
import com.hola.serverSide.appInterface.appi.StatusUpdate;
import com.hola.serverSide.appInterface.appi.StatusUpdateResponse;
import com.hola.serverSide.appInterface.appi.ReachabilityRequest;
import com.hola.serverSide.appInterface.appi.ReachabilityResponse;
import com.hola.serverSide.appInterface.appi.GoingToBackground;
import com.hola.serverSide.appInterface.appi.ContactAddRequest;
import com.hola.serverSide.appInterface.appi.ContactAddResponse;
import com.hola.serverSide.appInterface.appi.CurrentCallRequest;
import com.hola.serverSide.appInterface.appi.CurrentCallResponse;
import com.hola.serverSide.appInterface.appi.HandleReachability;
import com.hola.serverSide.appInterface.appi.HandleStatus;
import com.hola.serverSide.appInterface.db.beans.CurrentStatus;
import com.hola.serverSide.appInterface.db.CurrentStatusHelper;
import com.hola.serverSide.appInterface.db.beans.ActiveCall;
import com.hola.serverSide.appInterface.db.ActiveCallsHelper;
import com.hola.serverSide.appInterface.db.beans.HolaAccount;
import com.hola.serverSide.appInterface.db.HolaAccountsHelper;
import com.hola.serverSide.appInterface.db.beans.AccountFriend;
import com.hola.serverSide.appInterface.db.AccountFriendsHelper;
import com.hola.serverSide.appInterface.db.AccountPoliciesHelper;
import com.hola.serverSide.appInterface.db.beans.AccountPolicy;
import com.hola.serverSide.appInterface.db.AccountHandlesHelper;
import com.hola.serverSide.appInterface.db.beans.AccountHandles;
import com.hola.serverSide.appInterface.policy.PolicyHelper;
import com.hola.serverSide.appInterface.policy.Policy;
import com.hola.serverSide.appInterface.call.Location;
import com.hola.serverSide.appInterface.policy.Availability;
import com.hola.serverSide.appInterface.call.ProtoCall;
import com.hola.serverSide.appInterface.policy.ParticipantInfo;
import com.hola.serverSide.appInterface.appi.Contact;

public class HSMActiveHAPPIState extends HSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HSMActiveHAPPIState.class.getName ());
    // Eager initialization
    private static final HSMActiveHAPPIState instance = new HSMActiveHAPPIState  ();

    private static final ObjectMapper objectMapper = new ObjectMapper ();

    private HSMActiveHAPPIState ()
    {
	super (HSMState.ActiveHAPPI);
	//setState (HSMState.ActiveHAPPI);
    }
    
    public static HSMActiveHAPPIState getInstance ()
    {
	return instance;
    }

    public void processReceivedInfoRequest (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	//ObjectMapper objectMapper = new ObjectMapper ();
	HolaAccount holaAccount = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (peerState.getPeer ().getUsernameOrEmail ());
	InfoResponse infoResponse = null;
	List <Contact> contactsForUser = HolaAccountsHelper.getContactsForUser (peerState.getPeer ().getUsernameOrEmail ());

	if (holaAccount == null)
	    {
		log.error ("Hola account for peerState for {} is NULL!", peerState.getPeer ().getUsernameOrEmail ());
		//return HSMEvent.Error;
	    }
	
	if (contactsForUser != null)
	    {
		String extensionString = Integer.toString (holaAccount.getExtension ());
		// infoResponse = new InfoResponse ("52.87.234.4", Integer.toString (holaAccount.getExtension ()), 
		// 				 "H0141wP" + Integer.toString (holaAccount.getExtension ()) + "P4$$w0rd",
		infoResponse = new InfoResponse (extensionString, "54.88.13.192", extensionString, 
						 //infoResponse = new InfoResponse (extensionString, "54.236.214.86", extensionString, 
						 "H0141wP" + extensionString + "P4$$w0rd", "+17812096264", contactsForUser);
	    }
	else
	    {
		String extensionString = Integer.toString (holaAccount.getExtension ());
		// infoResponse = new InfoResponse ("52.87.234.4", Integer.toString (holaAccount.getExtension ()), 
		// 				 "H0141wP" + Integer.toString (holaAccount.getExtension ()) + "P4$$w0rd",
		infoResponse = new InfoResponse (extensionString, "54.88.13.192", extensionString, 
						 //infoResponse = new InfoResponse (extensionString, "54.236.214.86", extensionString, 
						 "H0141wP" + extensionString + "P4$$w0rd", "+17812096264", null);
	    }

	log.debug ("Sending InfoResponse {} to peer {}", infoResponse.toJsonString (), peerState.getUUID ());
	peerState.getSession ().getAsyncRemote ().sendText (infoResponse.toJsonString ());

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedStatusUpdate (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	StatusUpdate statusUpdate = (StatusUpdate) event;
	//ObjectMapper objectMapper = new ObjectMapper ();
	String handleStatusesAsJson = null;
	try
	    {
		handleStatusesAsJson = objectMapper.writeValueAsString (statusUpdate.getHandleStatuses ());
	    }
	catch (JsonProcessingException e)
	    {
		e.printStackTrace ();
	    }

	CurrentStatus currentStatus = 
	    new CurrentStatus (statusUpdate.getUsernameOrEmail (),
			       HolaAccountsHelper.getExtensionForUsernameOrEmail (statusUpdate.getUsernameOrEmail ()),
			       statusUpdate.getDoNotDisturb (), 
			       statusUpdate.getVoice (), 
			       statusUpdate.getText (), 
			       statusUpdate.getVideo (), 0);

	AccountHandlesHelper.updateAccountHandlesForUsernameOrEmail (statusUpdate.getUsernameOrEmail (), handleStatusesAsJson);
	log.debug ("Wrote the following handleStatusesAsJson {}", handleStatusesAsJson);
	int returnCode = CurrentStatusHelper.putCurrentStatusInCurrentStatusDB (currentStatus);
	if (returnCode >= 1)
	    {
		StatusUpdateResponse statusUpdateResponse = new StatusUpdateResponse ("Success");
		sendStatusUpdateResponse (peerState, event, statusUpdateResponse);

		// Also update the handles part of the hola account:
		HolaAccountsHelper.updateHolaAccountHandles (peerState.getPeer ().getUsernameOrEmail (), handleStatusesAsJson);
		// Next, find all the friends of the person sending the status update:"
		List <AccountFriend> friends = 
		    AccountFriendsHelper.getFriendsForUsernameOrEmail (statusUpdate.getUsernameOrEmail ());
		if (friends != null)
		    {
			for (AccountFriend friend: friends)
			    {
				log.debug ("Reachability update sent to {}, who is {}'s friend", 
					   friend.getFriend (), statusUpdate.getUsernameOrEmail ());
				List <APPIPeerState> friendsPeerStates = 
				    APPIPeerState.getPeerStateFromUsernameOrEmail (friend.getFriend ());

				//////////////
				if (friendsPeerStates.size () == 0)
				    {
					log.debug ("Friend {} is not connected in anyway", friend.getFriend ());
				    }
				else
				    {
					for (APPIPeerState friendsPeerState: friendsPeerStates)
					    {
						if (friendsPeerState != null)
						    {
							ReachabilityRequest pseudoReachabilityRequest = 
							    new ReachabilityRequest (statusUpdate.getUsernameOrEmail ());
							Runnable appiEventProcessor = 
							    new APPIEventProcessorRunnable (friendsPeerState, 
											    pseudoReachabilityRequest);
							Thread t = new Thread (appiEventProcessor);
							t.start ();
						    }
						else
						    {
							log.debug ("Friend {}'s peer state null; skipping reachability update",
								   friend.getFriend ());
						    }
					    }
				    }
				/////////////////
			    }
		    }
		else
		    {
			// Nobody to beam this new information to!
		    }
		log.debug ("StatusUpdateResponse {}", statusUpdateResponse.toJsonString ());

		//return HSMEvent.Success;
	    }
	else
	    {
		StatusUpdateResponse statusUpdateResponse = new StatusUpdateResponse ("Failure");
		log.error ("StatusUpdateResponse {}", statusUpdateResponse.toJsonString ());
		sendStatusUpdateResponse (peerState, event, statusUpdateResponse);

		//return HSMEvent.Error;
	    }       

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedReachabilityRequest (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	ReachabilityRequest reachabilityRequest = (ReachabilityRequest) event;	
	ReachabilityResponse reachabilityResponse = null;//new ReachabilityResponse ("false", reachabilities);
	ProtoCall protoCall = new ProtoCall (new ParticipantInfo (peerState.getPeer ().getUsernameOrEmail (), LocalTime.now (),
								  new Location ()),
					     new ParticipantInfo (reachabilityRequest.getUsernameOrEmail (), LocalTime.now (),
								  new Location ()));						  
	log.debug ("Caller->Callee {}->{}", protoCall.getCallerInfo ().getID (), protoCall.getCalleeInfo ().getID ());

	CurrentStatus currentStatus = CurrentStatusHelper.getCurrentStatusFromCurrentStatusDBByUsernameOrEmail 
	    (protoCall.getCalleeInfo ().getID ()); // Callee's current status

	HolaAccount callee = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (protoCall.getCalleeInfo ().getID ());
	if (callee == null)
	    {
		log.debug ("Callee is ULLLLLLLLLL");
	    }
	List <Policy> calleePolicies = AccountPoliciesHelper.getPolicyListForUsernameOrEmail (callee.getUsernameOrEmail ());

	if (currentStatus == null)
	    {
		log.debug ("Current status was null for user {}!", protoCall.getCalleeInfo ().getID ());
		currentStatus = new CurrentStatus (protoCall.getCalleeInfo ().getID (), 9999,
						   false, true, true, true, 0);
	    }
	log.debug ("Current status for callee: {}", currentStatus.toJsonString ());
	if (currentStatus.getDoNotDisturb ())
	    {
		reachabilityResponse = new ReachabilityResponse (true, protoCall.getCalleeInfo ().getID (), 
								 null); // Because other reachabilities do not matter
	    }
	else
	    {
		AccountHandles accountHandles = 
		    AccountHandlesHelper.getAccountHandlesByUsernameOrEmail (protoCall.getCalleeInfo ().getID ());
		log.debug ("Got accountHandles {}", accountHandles.toJsonString ());
		List <Availability> allAvailabilities = PolicyHelper.getAllAvailabilities (protoCall);
		log.debug ("Availabilities size (initial) = {}", allAvailabilities.size ());
		allAvailabilities = PolicyHelper.getAvailabilitiesByDevice (allAvailabilities, "mobile");
		log.debug ("Availabilities size (filtered by mobile) = {}", allAvailabilities.size ());
		if (!currentStatus.getVoice ())
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByMethod (allAvailabilities, "voice");
		    }
		log.debug ("Availabilities size (filter by voice) = {}", allAvailabilities.size ());
		if (!currentStatus.getText ())
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByMethod (allAvailabilities, "text");
		    }
		log.debug ("Availabilities size (filter by text) = {}", allAvailabilities.size ());
		if (!currentStatus.getVideo ())
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByMethod (allAvailabilities, "video");
		    }
		log.debug ("Availabilities size (filter by video) = {}", allAvailabilities.size ());
		if (!accountHandles.getStatusForApp ("Google"))
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByApp (allAvailabilities, "google");
		    }
		log.debug ("Availabilities size (filter by google) = {}", allAvailabilities.size ());
		if (!accountHandles.getStatusForApp ("Skype"))
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByApp (allAvailabilities, "skype");
		    }
		log.debug ("Availabilities size (filter by skype) = {}", allAvailabilities.size ());
		if (!accountHandles.getStatusForApp ("LandlineE164"))
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByApp (allAvailabilities, "landline");
		    }
		log.debug ("Availabilities size (filter by landline) = {}", allAvailabilities.size ());
		if (!accountHandles.getStatusForApp ("MobileE164"))
		    {
			allAvailabilities = PolicyHelper.excludeAvailabilitiesByApp (allAvailabilities, "mobile");
		    }
		log.debug ("Availabilities size (filter by mobile) = {}", allAvailabilities.size ());
		reachabilityResponse = new ReachabilityResponse ();
		reachabilityResponse.setUsernameOrEmail (protoCall.getCalleeInfo ().getID ());
		reachabilityResponse.setDoNotDisturb (false);
		// List <HandleReachability> reachabilities = new ArrayList <HandleReachability> ();

		// for (Availability a: allAvailabilities)
		//     {
		// 	log.debug ("Adding {} to ReachabilityResponse", a.toJsonString ());
		// 	HandleReachability handleReachability = new HandleReachability (a.getApp (),
		// 									"",
		// 									//callee.getHandleByApp (a.getApp ()),
		// 									a.getMethod (), "AVAILABLE");
		// 	reachabilities.add (handleReachability);
		//     }
		    //List <HandleReachability> expandedReachabilities = 
		    // PolicyHelper.getExpandedHandleReachabilities (protoCall.getCalleeInfo ().getID (), reachabilities);
		List <HandleReachability> expandedReachabilities = new ArrayList <HandleReachability> ();
		List <HandleStatus> handleStatuses = null;

		//ObjectMapper objectMapper = new ObjectMapper ();

		try
		    {
			handleStatuses = objectMapper.readValue (accountHandles.getHandles (), 
							   new TypeReference<List <HandleStatus>> () {});
			
		    }
		catch (IOException e)
		    {
			log.error ("Mapping to List <HandleStatus> did not work for accountHandles.getHandles ()");
			e.printStackTrace ();
		    }

		boolean voipVoiceShouldBePresent = false;

		for (HandleStatus handleStatus: handleStatuses)
		    {
			HandleReachability handleReachability = null;
			String handle = "";
			log.debug ("Processing app {}", handleStatus.getApp ());
			if (handleStatus.getApp ().equals ("voip"))
			    {
				handle = Integer.toString (callee.getExtension ());
			    }
			else
			    {
				handle = handleStatus.getHandle ();
			    }

			if (handleStatus.getStatus ())
			    {
				if (handleStatus.getMethod ().equals ("voice") && 
				    ((handleStatus.getApp ().equals ("landline") || 
				      handleStatus.getApp ().equals ("mobile")   || /// Add every other E164 tech here
				      handleStatus.getApp ().equals ("skypeNumber")   || /// Add every other E164 tech here
				      handleStatus.getApp ().equals ("googleVoice")   || /// Add every other E164 tech here
				      handleStatus.getApp ().equals ("voip"))))
				    {
					voipVoiceShouldBePresent = true;
					// Don't add the handle into expandedReachabilities
					continue;
				    }
				else
				    {
					handleReachability = new HandleReachability (handleStatus.getApp (), handle,
										     //handleStatus.getHandle (),
										     handleStatus.getMethod (), "AVAILABLE");
				    }
			    }
			else
			    {
				handleReachability = new HandleReachability (handleStatus.getApp (), handle,
									     //handleStatus.getHandle (),
									     handleStatus.getMethod (), "NOT_AVAILABLE");
			    }
			expandedReachabilities.add (handleReachability);
		    }	       

		if (voipVoiceShouldBePresent)		    
		    {
			expandedReachabilities.add (new HandleReachability ("voip", 
									    Integer.toString (callee.getExtension ()), 
									    "voice", "AVAILABLE"));
		    }

		reachabilityResponse.setReachabilities (expandedReachabilities);
	    }

	log.debug ("Sending ReachabilityResponse {}", reachabilityResponse.toJsonString ());
	peerState.getSession ().getAsyncRemote ().sendText (reachabilityResponse.toJsonString ());
	//return HSMEvent.Success;

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedGoingToBackground (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	peerState.changeState (getStateForStateName (HSMState.WaitForWakeUp));

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedContactAddRequest (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	ContactAddRequest contactAddRequest = (ContactAddRequest) event;
	HolaAccount holaAccount = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (contactAddRequest.getUsernameOrEmail ());
	
	if (holaAccount != null)
	    {
		ContactAddResponse contactAddResponse = new 
		    ContactAddResponse (contactAddRequest.getMsgId (),
					true, "Found contact with username",
					new Contact (holaAccount.getFirstname (),
						     holaAccount.getLastname (),
						     holaAccount.getUsernameOrEmail (),
						     Integer.toString (holaAccount.getExtension ())));

		AccountFriendsHelper.putAccountFriend (peerState.getPeer ().getUsernameOrEmail (), holaAccount.getUsernameOrEmail ());

		peerState.getSession ().getAsyncRemote ().sendText (contactAddResponse.toJsonString ());
		//return HSMEvent.Success;
	    }
	else
	    {
		ContactAddResponse contactAddResponse = new ContactAddResponse (contactAddRequest.getMsgId (),
										false, "Did not find contact with username",
										null);

		peerState.getSession ().getAsyncRemote ().sendText (contactAddResponse.toJsonString ());
		//return HSMEvent.Success;
	    }
    

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedCurrentCallRequest (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	HolaAccount holaAccount = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (peerState.getPeer ().getUsernameOrEmail ());
	ActiveCall activeCall   = ActiveCallsHelper.getActiveCallByExtension (holaAccount.getExtension ());
	String currentCallTechnology = null, handoverTechnology = null;
	
	if (activeCall != null)
	    {
		if (peerState.getPeer () != null)
		    {
			log.debug ("CurrentCallResponse processing: checking activeCall for {}/{}", 
				   peerState.getPeer ().getUsernameOrEmail (), activeCall.toJsonString ());
		    }
		else
		    {
			log.error ("Peer state has no peer!");
		    }
		/*
		  if holaAccount extension is the same as the calle(e/r) ID and calle(e/r) handle, then leg on voip. else, leg
		  on pstn
		 */
	    }
	else
	    {
		log.debug ("Active call is null in process current call request");
	    }

	if ((activeCall != null) && (activeCall.getZombie () != true))
	    {
		String handoverNumber;
		if (activeCall.getCalleeID ().equals (Integer.toString (holaAccount.getExtension ())))
		    {
			handoverNumber = activeCall.getCallerID ();
			if (activeCall.getCalleeHandle ().equals (activeCall.getCalleeID ()))
			    {
				currentCallTechnology = "voip";
				handoverTechnology = "pstn";
			    }
			else
			    {
				currentCallTechnology = "pstn";
				handoverTechnology = "voip";			    }
		    }
		//else
		else if (activeCall.getCallerID ().equals (Integer.toString (holaAccount.getExtension ())))
		    {
			handoverNumber = activeCall.getCalleeID ();
			if (activeCall.getCallerHandle ().equals (activeCall.getCallerID ()))
			    {
				currentCallTechnology = "voip";
				handoverTechnology = "pstn";
			    }
			else
			    {
				currentCallTechnology = "pstn";
				handoverTechnology = "voip";
			    }
		    }
		else
		    {
			handoverNumber = "";
		    }

		if (handoverNumber.substring (0,1).equals ("+")) // The handover could be to an E164
		    {
			handoverNumber = "9" + handoverNumber.substring (1);
		    }

		CurrentCallResponse currentCallResponse = new CurrentCallResponse  (currentCallTechnology, handoverTechnology, 
										    handoverNumber, "");

		peerState.getSession ().getAsyncRemote ().sendText (currentCallResponse.toJsonString ());
		log.debug ("Sending CurrentCallResponse {} to {}", currentCallResponse, peerState.getPeer ().getUsernameOrEmail ());
		//return HSMEvent.Success;
	    }
	else
	    {
		CurrentCallResponse currentCallResponse = new CurrentCallResponse  ("", "", "", ""); // there is no active call

		peerState.getSession ().getAsyncRemote ().sendText (currentCallResponse.toJsonString ());
		log.debug ("Sending CurrentCallResponse null {} to {}", currentCallResponse, peerState.getPeer ().getUsernameOrEmail ());
		//return HSMEvent.Success;
	    }

	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }

    public void processReceivedReachabilityUpdateRequest (APPIPeerState peerState, APPIEvent event)
    {
	//process (peerState, event);
	
	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }


    public void sendStatusUpdateResponse (APPIPeerState peerState, APPIEvent event, StatusUpdateResponse statusUpdateResponse)
    {
	//ObjectMapper objectMapper = new ObjectMapper ();
	String statusUpdateResponseString = null;

	try
	    {
		statusUpdateResponseString = objectMapper.writeValueAsString (statusUpdateResponse);
	    }
	catch (JsonProcessingException e)
	    {
		// For the moment, if this happens, I don't really care :-o
		e.printStackTrace ();
	    }
	
	peerState.getSession ().getAsyncRemote ().sendText (statusUpdateResponseString);
    }

    /*
    public void process (APPIPeerState peerState, APPIEvent event)
    {
	switch (peerState.getLastHSMEvent ())
	    {
	    case ReceivedInfoRequest:
		peerState.changeState (getStateForStateName (HSMState.WaitForInfoRequestProcessing));
		peerState.processReceivedInfoRequest (event);
		break;
	    case ReceivedStatusUpdate:
		peerState.changeState (getStateForStateName (HSMState.WaitForStatusUpdateProcessing));
		peerState.processReceivedStatusUpdate (event);
		break;
	    case ReceivedReachabilityRequest:
		peerState.changeState (getStateForStateName (HSMState.WaitForReachabilityRequestProcessing));
		peerState.processReceivedReachabilityRequest (event);
		break;
	    case ReceivedGoingToBackground:
		peerState.changeState (getStateForStateName (HSMState.WaitForWakeUp));
		break; // Nothing else to do. Just park there!
	    case ReceivedContactAddRequest:
		peerState.changeState (getStateForStateName (HSMState.WaitForContactAddRequestProcessing));
		peerState.processReceivedContactAddRequest (event);
		break;
	    case ReceivedCurrentCallRequest:
		peerState.changeState (getStateForStateName (HSMState.WaitForCurrentCallRequestProcessing));
		peerState.processReceivedCurrentCallRequest (event);
		break;		
	    }


	if (peerState != null)
	    {
		//log.debug ("Peerstate.getCurrentState () is not null {}", peerState.toString ());
	    }


	log.debug ("{} Successfully processed {} event for peer {}: current state is {}", Thread.currentThread ().getId (),
		  event.getType (), peerState.getUUID (), peerState.getCurrentState ().getStateName ());
    }
    */
}
