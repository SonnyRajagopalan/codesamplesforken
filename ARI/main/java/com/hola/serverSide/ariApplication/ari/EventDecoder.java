/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.ari;

import java.io.IOException;

import javax.websocket.Decoder;
import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDecoder implements Decoder.Text<Event>
{
    private static final Logger log = LoggerFactory.getLogger (EventDecoder.class.getName ());

    private static final ObjectMapper objectMapper = new ObjectMapper ();

    @Override
    public void init (EndpointConfig config) 
    {
    }
    
    @Override
    public void destroy () 
    {
    }

    @Override
    public Event decode (String incomingString) throws DecodeException
    {
	/*
	  Parse into JSON, used Jackson to first get the type, if it exists. If it doesn't, then that's an error
	  (some other type of message, which has no business being sent over the WebSocket interface).

	  Deduce the exact type of message, and pack the message, and send it over to 
	  Has to be a message. If not, throw an exception
	*/
	//ObjectMapper mapper = new ObjectMapper ();

	boolean eventJSONReceived = true;
	Event ariEvent = null;

	//log.debug ("\n\n\n" + "Thread ID " + Thread.currentThread ().getId () + " Received : " + incomingString);
	log.info ("Received : " + incomingString);
	try
	    {
		JsonNode incoming      = objectMapper.readTree (incomingString);
		JsonNode typeNode      = incoming.path ("type");
		String eventTypeString = typeNode.asText ();
		log.debug ("Thread ID " + Thread.currentThread ().getId () + 
				    " EventDecoder: Event type is " + typeNode.asText ());

		switch (eventTypeString)
		    {
		    case "ApplicationReplaced":
			ariEvent = objectMapper.readValue (incomingString, ApplicationReplaced.class);
			break;
		    case "BridgeAttendedTransfer":
			ariEvent = objectMapper.readValue (incomingString, BridgeAttendedTransfer.class);
			break;
		    case "BridgeBlindTransfer":
			ariEvent = objectMapper.readValue (incomingString, BridgeBlindTransfer.class);
			break;
		    case "BridgeCreated":
			ariEvent = objectMapper.readValue (incomingString, BridgeCreated.class);
			break;
		    case "BridgeDestroyed":
			ariEvent = objectMapper.readValue (incomingString, BridgeDestroyed.class);
			break;
		    case "BridgeMerged":
			ariEvent = objectMapper.readValue (incomingString, BridgeMerged.class);
			break;
		    case "ChannelCallerId":
			ariEvent = objectMapper.readValue (incomingString, ChannelCallerId.class);
			break;
		    case "ChannelConnectedLine":
			ariEvent = objectMapper.readValue (incomingString, ChannelConnectedLine.class);
			break;
		    case "ChannelCreated":
			ariEvent = objectMapper.readValue (incomingString, ChannelCreated.class);
			break;
		    case "ChannelDestroyed":
			ariEvent = objectMapper.readValue (incomingString, ChannelDestroyed.class);
			break;
		    case "ChannelDialplan":
			ariEvent = objectMapper.readValue (incomingString, ChannelDialplan.class);
			break;
		    case "ChannelDtmfReceived":
			ariEvent = objectMapper.readValue (incomingString, ChannelDtmfReceived.class);
			break;
		    case "ChannelEnteredBridge":
			ariEvent = objectMapper.readValue (incomingString, ChannelEnteredBridge.class);
			break;
		    case "ChannelHangupRequest":
			ariEvent = objectMapper.readValue (incomingString, ChannelHangupRequest.class);
			break;
		    case "ChannelLeftBridge":
			ariEvent = objectMapper.readValue (incomingString, ChannelLeftBridge.class);
			break;
		    case "ChannelStateChange":
			ariEvent = objectMapper.readValue (incomingString, ChannelStateChange.class);
			break;
		    case "ChannelTalkingFinished":
			ariEvent = objectMapper.readValue (incomingString, ChannelTalkingFinished.class);
			break;
		    case "ChannelTalkingStarted":
			ariEvent = objectMapper.readValue (incomingString, ChannelTalkingStarted.class);
			break;
		    case "ChannelUserevent":
			ariEvent = objectMapper.readValue (incomingString, ChannelUserevent.class);
			break;
		    case "ChannelVarset":
			ariEvent = objectMapper.readValue (incomingString, ChannelVarset.class);
			break;
		    case "DeviceStateChanged":
			ariEvent = objectMapper.readValue (incomingString, DeviceStateChanged.class);
			break;
		    case "Dial":
			ariEvent = objectMapper.readValue (incomingString, Dial.class);
			break;
		    case "EndpointStateChange":
			ariEvent = objectMapper.readValue (incomingString, EndpointStateChange.class);
			break;
		    case "PlaybackFinished":
			ariEvent = objectMapper.readValue (incomingString, PlaybackFinished.class);
			break;
		    case "PlaybackStarted":
			ariEvent = objectMapper.readValue (incomingString, PlaybackStarted.class);
			break;
		    case "RecordingFailed":
			ariEvent = objectMapper.readValue (incomingString, RecordingFailed.class);
			break;
		    case "RecordingFinished":
			ariEvent = objectMapper.readValue (incomingString, RecordingFinished.class);
			break;
		    case "RecordingStarted":
			ariEvent = objectMapper.readValue (incomingString, RecordingStarted.class);
			break;
		    case "StasisEnd":
			ariEvent = objectMapper.readValue (incomingString, StasisEnd.class);
			break;
		    case "StasisStart":
			ariEvent = objectMapper.readValue (incomingString, StasisStart.class);
			break;
		    case "TextMessageReceived":
			ariEvent = objectMapper.readValue (incomingString, TextMessageReceived.class);
			break;

		    default:
			log.debug ("Thread ID " + Thread.currentThread ().getId () + " Some other type of message");
		    }
	    }
	catch (IOException e)
	    {
		eventJSONReceived = false;
	    }

	if (!eventJSONReceived)
	    {
		throw new DecodeException (incomingString, 
					   "The incoming message WebSocket message could not be parsed as an ARI Event object");
	    }
	return ariEvent;
    }

    @Override
	public boolean willDecode (String incomingMessage)
    {
	return true;
    }
}
