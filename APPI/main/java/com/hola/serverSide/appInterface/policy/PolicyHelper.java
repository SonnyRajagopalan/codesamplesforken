package com.hola.serverSide.appInterface.policy;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific
 */
import com.hola.serverSide.appInterface.db.beans.HolaAccount;
import com.hola.serverSide.appInterface.db.HolaAccountsHelper;
import com.hola.serverSide.appInterface.db.AccountPoliciesHelper;
import com.hola.serverSide.appInterface.call.ProtoCall;
import com.hola.serverSide.appInterface.appi.HandleReachability;

public class PolicyHelper
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (PolicyHelper.class.getName ());
    private static final ObjectMapper objectMapper = new ObjectMapper ();
    public static List <Policy> getPoliciesFromPolicyString (String policyJsonString)
    {
	List <Policy> policiesForThisPerson = new ArrayList <Policy> ();
	try
	    {
		//System.out.println ("Policy file path is " + policyFilePath);
		//System.out.println ("Policy string is " + policyJsonString);
		//File policyFile = new File (policyFilePath);

		//ObjectMapper objectMapper = new ObjectMapper ();
		//JsonNode policiesJson = objectMapper.readTree (policyFile);
		JsonNode policiesJson = objectMapper.readTree (policyJsonString);
		JsonNode policyJsons = policiesJson.path ("policies");

		for (JsonNode policyJson : policyJsons)
		    {
			JsonNode conditions                     = policyJson.path ("conditions");
			String policyName                       = policyJson.path ("name").asText ();
			Policy aPolicy                          = new Policy (policyName);
			List <Condition>    policyConditions    = new ArrayList <Condition> ();
			List <Action>       trueActions         = new ArrayList <Action> ();
			List <Action>       falseActions        = new ArrayList <Action> ();
			List <Availability> trueAvailabilities  = new ArrayList <Availability> ();
			List <Availability> falseAvailabilities = new ArrayList <Availability> ();

			//System.out.println ("Name of policy is " + policyName);
			for (JsonNode condition : conditions)
			    {
				String conditionType = condition.path ("type").asText ();
				String compareType   = condition.path ("compare").asText ();
				//System.out.println ("Condition is " + conditionType);
				switch (conditionType)
				    {
				    case "TIME_BASED_CONDITION":
					{
					    //TimeCondition timeCondition = new TimeCondition ();
					    JsonNode durationsJson = condition.path ("durations");
					    List <Duration> durationsForTimeCondition = new ArrayList <Duration> ();
					    for (JsonNode durationJson : durationsJson)
						{
						    String startString = durationJson.path ("start").asText ();
						    String endString = durationJson.path ("end").asText ();
						    Duration thisDuration = new Duration (startString, endString);

						    durationsForTimeCondition.add(thisDuration);
						    //System.out.println (startString  + ", " + endString);
						}
					    TimeComparisonList timeComparisonList = new 
						TimeComparisonList (durationsForTimeCondition);
					    TimeCondition timeCondition = new TimeCondition ("TIME_BASED_CONDITION",
											     timeComparisonList,
											     compareType);
					    //timeCondition.print ();
					    policyConditions.add (timeCondition);
					}
					break;
				    case "USER_BASED_CONDITION":
					{
					    JsonNode usersJson = condition.path ("users");
					    List <String> usersForUserCondition = new ArrayList <String> ();
					    for (JsonNode userJson : usersJson)
						{
						    usersForUserCondition.add (userJson.asText ());
						}
					    UserComparisonList userComparisonList = new 
						UserComparisonList (usersForUserCondition);
					    UserCondition userCondition = new UserCondition ("USER_BASED_CONDITION",
											     userComparisonList, 
											     compareType);
					    //userCondition.print ();
					    policyConditions.add (userCondition);
					}
					break;
				    default:
					{
					    // Error
					}
				    }
			    }// End of conditions

			JsonNode actionJsons = policyJson.path ("actions");
			JsonNode trueActionsNode  = actionJsons.path ("true");
			JsonNode falseActionsNode = actionJsons.path ("false");

			for (JsonNode trueAction : trueActionsNode)
			    {
				String trueActionString = trueAction.asText ();
				//System.out.println (trueActionString);
				trueActions.add (ActionType.getActionFromTypeString (trueActionString));
			    }

			for (JsonNode falseAction : falseActionsNode)
			    {			    
				String falseActionString = falseAction.asText ();
				//System.out.println (falseActionString);
				falseActions.add (ActionType.getActionFromTypeString (falseActionString));
			    }
			//System.out.println ("\t-> True  action: " + trueActionsNode);
			//System.out.println ("\t-> False action: " + falseActionsNode);

			JsonNode availabilitiesNodes     = policyJson.path ("availabilities");
			JsonNode trueAvailabilitiesNode  = availabilitiesNodes.path ("true");
			JsonNode falseAvailabilitiesNode = availabilitiesNodes.path ("false");
			//System.out.println ("Availabilities = " + availabilitiesNode);
			for (JsonNode trueAvailability: trueAvailabilitiesNode)
			    {				
				//String availabilityString = availability.asText ();
				String deviceString = trueAvailability.path ("device").asText ();
				String appString    = trueAvailability.path ("app").asText ();
				String methodString = trueAvailability.path ("method").asText ();
				Availability oneAppsAvailabilityOnOneDevice = new Availability (deviceString, appString, 
												methodString);
				trueAvailabilities.add (oneAppsAvailabilityOnOneDevice);
				//System.out.println ("\t\tDevice: " + deviceString + ", app: " + appString);
			    }

			for (JsonNode falseAvailability: falseAvailabilitiesNode)
			    {				
				//String availabilityString = availability.asText ();
				String deviceString = falseAvailability.path ("device").asText ();
				String appString    = falseAvailability.path ("app").asText ();
				String methodString = falseAvailability.path ("method").asText ();
				Availability oneAppsAvailabilityOnOneDevice = new Availability (deviceString, appString, 
												methodString);
				falseAvailabilities.add (oneAppsAvailabilityOnOneDevice);
				//System.out.println ("\t\tDevice: " + deviceString + ", app: " + appString);
			    }
			
			aPolicy.setConditions (policyConditions);
			aPolicy.setTrueActions (trueActions);
			aPolicy.setFalseActions (falseActions);
			aPolicy.setTrueAvailabilities (trueAvailabilities);
			aPolicy.setFalseAvailabilities (falseAvailabilities);

			policiesForThisPerson.add (aPolicy);
		    }

	    }
	catch (JsonMappingException e) 
	    {
		log.debug ("JSON parsing exception. Check policy JSON for user");
		e.printStackTrace();
	    } 
	catch (IOException e) 
	    {
		e.printStackTrace(); 
	    }

	return policiesForThisPerson;
    }

    public static List <Availability> getAllAvailabilities (ProtoCall call, String...options)
    {
	List <Availability> allAvailabilities = new ArrayList <Availability> ();
	//User callee = UserHelper.getUserFromUserDBByUserID (call.getCalleeInfo ().getID ());
	HolaAccount callee = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (call.getCalleeInfo ().getID ());
	String policyString = 
	    AccountPoliciesHelper.getAccountPolicyByUsernameOrEmail (callee.getUsernameOrEmail ()).getPolicies ();
	List <Policy> calleePolicies = getPoliciesFromPolicyString (policyString);
	log.debug ("Have {} policies", calleePolicies.size ());
	for (Policy onePolicy : calleePolicies)
	    {
		List <Availability> availabilitiesFromOnePolicy = onePolicy.getReachability (call, options);
		for (Availability a: availabilitiesFromOnePolicy)
		    {
			log.debug (a.toString ());
		    }
		allAvailabilities.addAll (availabilitiesFromOnePolicy);
	    }
	return allAvailabilities;
    }

    public static List <Availability> excludeAvailabilitiesByApp (List <Availability> allAvailabilities, String app)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getApp ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyAppAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getApp ().equals (app))
				    {
				    }
				else
				    {
					filteredAvailabilities.add (a);
				    }
			    }
		    }
		else
		    {
			if (availability.getApp ().equals (app))
			    {
			    }
			else
			    {
				filteredAvailabilities.add (availability);
			    }
		    }
	    }
	return filteredAvailabilities;
    }

    public static List <Availability> getAvailabilitiesByApp (List <Availability> allAvailabilities, String app)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getApp ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyAppAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getApp ().equals (app))
				    {
					filteredAvailabilities.add (a);
				    }
				else
				    {
				    }
			    }
		    }
		else
		    {
			if (availability.getApp ().equals (app))
			    {
				filteredAvailabilities.add (availability);
			    }
			else
			    {
			    }
		    }
	    }
	return filteredAvailabilities;
    }


    public static List <Availability> excludeAvailabilitiesByMethod (List <Availability> allAvailabilities, String method)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getMethod ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyMethodAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getMethod ().equals (method))
				    {
				    }
				else
				    {
					filteredAvailabilities.add (a);
				    }
			    }
		    }
		else
		    {
			if (availability.getMethod ().equals (method))
			    {
			    }
			else
			    {
				filteredAvailabilities.add (availability);
			    }
		    }
	    }
	return filteredAvailabilities;
    }

    public static List <Availability> getAvailabilitiesByMethod (List <Availability> allAvailabilities, String method)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getMethod ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyMethodAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getMethod ().equals (method))
				    {
					filteredAvailabilities.add (a);
				    }
				else
				    {
				    }
			    }
		    }
		else
		    {
			if (availability.getMethod ().equals (method))
			    {
				filteredAvailabilities.add (availability);
			    }
			else
			    {
			    }
		    }
	    }
	return filteredAvailabilities;
    }
    
    public static List <Availability> excludeAvailabilitiesByDevice (List <Availability> allAvailabilities, String device)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getDevice ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyDeviceAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getDevice ().equals (device))
				    {
				    }
				else
				    {
					filteredAvailabilities.add (a);
				    }
			    }
		    }
		else
		    {
			if (availability.getDevice ().equals (device))
			    {
			    }
			else
			    {
				filteredAvailabilities.add (availability);
			    }
		    }
	    }
	return filteredAvailabilities;
    }    

    public static List <Availability> getAvailabilitiesByDevice (List <Availability> allAvailabilities, String device)
    {
	List <Availability> filteredAvailabilities = new ArrayList <Availability> ();
	for (Availability availability: allAvailabilities)
	    {
		if (availability.getDevice ().equals ("any"))
		    {
			List <Availability> expandedAvailabilities = Availability.expandAnyDeviceAvailability (availability);

			for (Availability a: expandedAvailabilities)
			    {
				if (a.getDevice ().equals (device))
				    {
					filteredAvailabilities.add (a);
				    }
				else
				    {
				    }
			    }
		    }
		else
		    {
			if (availability.getDevice ().equals (device))
			    {
				filteredAvailabilities.add (availability);
			    }
			else
			    {
			    }
		    }
	    }
	return filteredAvailabilities;
    }    

    // public static List <HandleReachability> getExpandedHandleReachabilities (String userID, 
    // 									     List <HandleReachability> reachabilities)
    // {
    // 	List <HandleReachability> expandedReachabilities = new ArrayList <HandleReachability> ();
    // 	List <HandleReachability> appExpandedReachabilities = new ArrayList <HandleReachability> ();
	
    // 	for (HandleReachability hr: reachabilities)
    // 	    {
    // 		if (hr.getApp ().equals ("any"))		    
    // 		    {
    // 			List <HandleReachability> expandedHR = getExpandedAppReachabilities (userID, hr);
    // 			appExpandedReachabilities.addAll (expandedHR);
    // 		    }
    // 		else
    // 		    {
    // 			appExpandedReachabilities.add (hr);
    // 		    }
    // 	    }

    // 	for (HandleReachability hr: appExpandedReachabilities)
    // 	    {
    // 		if (hr.getMethod ().equals ("any"))		    
    // 		    {
    // 			List <HandleReachability> expandedHR = getExpandedMethodReachabilities (userID, hr);
    // 			expandedReachabilities.addAll (expandedHR);
    // 		    }
    // 	    }
    // 	return expandedReachabilities;
    // }

    // public static List <HandleReachability> getExpandedAppReachabilities (String userID,
    // 									  HandleReachability hr)
    // {
    // 	List <HandleReachability> appExpandedReachabilities = new ArrayList <HandleReachability> ();

    // 	if (hr.getApp ().equals ("any"))
    // 	    {
    // 		HandleReachability h1, h2, h3;
    // 		String skypeHandle  = UserHelper.getSkypeForUser  (userID);
    // 		String googleHandle = UserHelper.getGoogleForUser (userID);
    // 		String voipHandle = UserHelper.getVoIPNumericForUser (userID);
    // 		if (voipHandle != null)
    // 		    {
    // 			h3 = new HandleReachability ("voip", voipHandle, hr.getMethod (), "AVAILABLE");
    // 			appExpandedReachabilities.add (h3);
    // 		    }
    // 		if (skypeHandle != null)
    // 		    {
    // 			h1 = new HandleReachability ("skype", skypeHandle, hr.getMethod (), "AVAILABLE");
    // 			appExpandedReachabilities.add (h1);
    // 		    }
    // 		if (googleHandle != null)
    // 		    {
    // 			h1 = new HandleReachability ("skype", googleHandle, hr.getMethod (), "AVAILABLE");
    // 			appExpandedReachabilities.add (h1);
    // 		    }
    // 	    }
    // 	return appExpandedReachabilities;
    // }

    public static List <HandleReachability> getExpandedMethodReachabilities (String userID,
									  HandleReachability hr)
    {
	List <HandleReachability> methodExpandedReachabilities = new ArrayList <HandleReachability> ();

	if (hr.getMethod ().equals ("any"))
	    {
		HandleReachability h1, h2, h3;
		h1 = new HandleReachability (hr.getApp (), hr.getHandle (), "voice", "AVAILABLE");
		h2 = new HandleReachability (hr.getApp (), hr.getHandle (), "text", "AVAILABLE");
		h3 = new HandleReachability (hr.getApp (), hr.getHandle (), "video", "AVAILABLE");
		methodExpandedReachabilities.add (h1);
		methodExpandedReachabilities.add (h2);
		methodExpandedReachabilities.add (h3);
	    }

	return methodExpandedReachabilities;
    }

}
