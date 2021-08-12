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
 * Singleton for WaitForAddOrRemoveSIPRegistrationListenerState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.Event;
import com.hola.serverSide.ariApplication.ari.StasisStart;
import com.hola.serverSide.ariApplication.ari.ApplicationsARIAPI;

import com.sun.jersey.api.client.ClientResponse;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForAddOrRemoveSIPRegistrationListenerState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForAddOrRemoveSIPRegistrationListenerState.class.getName ());
    // Eager initialization
    private static final CSMWaitForAddOrRemoveSIPRegistrationListenerState instance = new CSMWaitForAddOrRemoveSIPRegistrationListenerState  ();

    private CSMWaitForAddOrRemoveSIPRegistrationListenerState ()
    {
	setState (CSMState.WaitForAddOrRemoveSIPRegistrationListener);
    }
    
    public static CSMWaitForAddOrRemoveSIPRegistrationListenerState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    @Override
    public void processError (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }
    
    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForUpdateActiveCallDB));
		call.getCurrentState ().processSuccess (call, event);
		break;
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.getCurrentState ().processError (call, event);
		break;
	    default:
		// Error event?
	    }
    }
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	ClientResponse endpointStateChangeSubscribeResponse;
	if (call.getLastCSMEvent () == CSMEvent.FromPSTNForExistingCall)
	    {
		// Subscribe to the event
		endpointStateChangeSubscribeResponse = ApplicationsARIAPI.subscribe ("psa", "?eventSource=endpoint:PJSIP");
	    }
	else
	    {
		// Unsubscribe from the event
		endpointStateChangeSubscribeResponse = ApplicationsARIAPI.unsubscribe ("psa", "?eventSource=endpoint:PJSIP");
	    }

	if (endpointStateChangeSubscribeResponse.getStatus () == 400)
	    {
		log.error ("Missing parameter; event source scheme not recognized.");
		return CSMEvent.Error;
	    }
	else if (endpointStateChangeSubscribeResponse.getStatus () == 404)
	    {
		log.error ("Application does not exist.");
		return CSMEvent.Error;
	    }
	else if (endpointStateChangeSubscribeResponse.getStatus () == 409)
	    {
		log.error ("Application not subscribed to event source.");
		return CSMEvent.Error;
	    }
	else if (endpointStateChangeSubscribeResponse.getStatus () == 422)
	    {
		log.error ("Event source does not exist.");
		return CSMEvent.Error;
	    }
	// Don't care about the current application state (for now)
	return CSMEvent.Success;
    }
}
