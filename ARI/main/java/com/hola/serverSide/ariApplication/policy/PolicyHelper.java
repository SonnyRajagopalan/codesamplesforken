package com.hola.serverSide.ariApplication.policy;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyHelper
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (PolicyHelper.class.getName ());

    public static List <Policy> getPoliciesFromPolicyString (String policyJsonString)
    {
	List <Policy> policiesForThisPerson = new ArrayList <Policy> ();
	try
	    {
		//log.debug ("Policy file path is " + policyFilePath);
		log.debug ("Policy string is " + policyJsonString);
		//File policyFile = new File (policyFilePath);

		ObjectMapper mapper = new ObjectMapper ();
		//JsonNode policiesJson = mapper.readTree (policyFile);
		JsonNode policiesJson = mapper.readTree (policyJsonString);
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

			//log.debug ("Name of policy is " + policyName);
			for (JsonNode condition : conditions)
			    {
				String conditionType = condition.path ("type").asText ();
				String compareType   = condition.path ("compare").asText ();
				//log.debug ("Condition is " + conditionType);
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
						    //log.debug (startString  + ", " + endString);
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
				//log.debug (trueActionString);
				trueActions.add (ActionType.getActionFromTypeString (trueActionString));
			    }

			for (JsonNode falseAction : falseActionsNode)
			    {			    
				String falseActionString = falseAction.asText ();
				//log.debug (falseActionString);
				falseActions.add (ActionType.getActionFromTypeString (falseActionString));
			    }
			//log.debug ("\t-> True  action: " + trueActionsNode);
			//log.debug ("\t-> False action: " + falseActionsNode);

			JsonNode availabilitiesNodes     = policyJson.path ("availabilities");
			JsonNode trueAvailabilitiesNode  = availabilitiesNodes.path ("true");
			JsonNode falseAvailabilitiesNode = availabilitiesNodes.path ("false");
			//log.debug ("Availabilities = " + availabilitiesNode);
			for (JsonNode trueAvailability: trueAvailabilitiesNode)
			    {				
				//String availabilityString = availability.asText ();
				String deviceString = trueAvailability.path ("device").asText ();
				String appString    = trueAvailability.path ("app").asText ();
				String methodString = trueAvailability.path ("method").asText ();
				Availability oneAppsAvailabilityOnOneDevice = new Availability (deviceString, appString, methodString);
				trueAvailabilities.add (oneAppsAvailabilityOnOneDevice);
				//log.debug ("\t\tDevice: " + deviceString + ", app: " + appString);
			    }

			for (JsonNode falseAvailability: falseAvailabilitiesNode)
			    {				
				//String availabilityString = availability.asText ();
				String deviceString = falseAvailability.path ("device").asText ();
				String appString    = falseAvailability.path ("app").asText ();
				String methodString = falseAvailability.path ("method").asText ();
				Availability oneAppsAvailabilityOnOneDevice = new Availability (deviceString, appString, methodString);
				falseAvailabilities.add (oneAppsAvailabilityOnOneDevice);
				//log.debug ("\t\tDevice: " + deviceString + ", app: " + appString);
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
		e.printStackTrace();
	    } 
	catch (IOException e) 
	    {
		e.printStackTrace(); 
	    }

	return policiesForThisPerson;
    }
}
