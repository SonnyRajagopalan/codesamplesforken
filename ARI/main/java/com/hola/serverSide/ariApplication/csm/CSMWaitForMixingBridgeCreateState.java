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
 * Singleton for WaitForMixingBridgeCreateState state of the Call State Machine
 */
package com.hola.serverSide.ariApplication.csm;

import com.hola.serverSide.ariApplication.call.Call;
import com.hola.serverSide.ariApplication.ari.BridgesARIAPI;
import com.hola.serverSide.ariApplication.ari.Bridge;
import com.hola.serverSide.ariApplication.ari.Event;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMWaitForMixingBridgeCreateState extends CSMBaseState
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (CSMWaitForMixingBridgeCreateState.class.getName ());
    // Eager initialization
    private static final CSMWaitForMixingBridgeCreateState instance = new CSMWaitForMixingBridgeCreateState  ();

    private CSMWaitForMixingBridgeCreateState ()
    {
	setState (CSMState.WaitForMixingBridgeCreate);
    }
    
    public static CSMWaitForMixingBridgeCreateState getInstance ()
    {
	return instance;
    }

    @Override
    public void processSuccess (Call call, Event event)
    {
	log.debug ("Call {}: processing event {}", call.getUUID (), event.getType ());
        process (call, event);
    }

    private void process (Call call, Event event)
    {
	switch (getTransitionForCallContext (call, event))
	    {
	    case Success:
		call.changeState (getStateForStateName (CSMState.WaitForConnectCallerAndCallee));
		call.processSuccess (event);
		break;
	    case Error:
		call.changeState (getStateForStateName (CSMState.Error));
		call.processError(event);
		break;
	    default:
		// Error event?
	    }
    }
    
    @Override
    public CSMEvent getTransitionForCallContext (Call call, Event event)
    {
	String query = "?type=mixing,dtmf_events";
	
	ClientResponse bridgeCreateResponse = BridgesARIAPI.create (query);

	if (bridgeCreateResponse.getStatus () != 200)
	    {
		log.error ("There was an issue with the bridge create for call {}", call.getUUID ());
		return CSMEvent.Error;
	    }

	String output = bridgeCreateResponse.getEntity (String.class);

	try
	    {
		ObjectMapper mapper = new ObjectMapper ();
		Bridge bridge = mapper.readValue (output, Bridge.class);

		call.setBridge (bridge);
	    }
	catch (IOException e)
	    {
		e.printStackTrace ();
	    }

	return CSMEvent.Success;
    }
}
