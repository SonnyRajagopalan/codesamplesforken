package com.hola.serverSide.appInterface.appi;
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

public class APPIEventDecoder implements Decoder.Text<APPIEvent>
{
    private static final Logger log = LoggerFactory.getLogger (APPIEventDecoder.class.getName ());
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
    public APPIEvent decode (String incomingString) throws DecodeException
    {
       /*
          Parse into JSON, used Jackson to first get the type, if it exists. If it doesn't, then that's an error
          (some other type of message, which has no business being sent over the WebSocket interface).

          Deduce the exact type of message, and pack the message, and send it over to 
          Has to be a message. If not, throw an exception
        */
        //ObjectMapper mapper = new ObjectMapper ();

        boolean messageJSONReceived = true;
        APPIEvent appiEvent = null;
	String eventTypeString = "NOT_YET_INITIALIZED";
        //System.out.println ("\n\n\n" + "Thread ID " + Thread.currentThread ().getId () + " Received : " + incomingString);
        log.debug ("Received : " + incomingString);
        try
            {
                JsonNode incoming = objectMapper.readTree (incomingString);
                JsonNode typeNode = incoming.path ("type");
                eventTypeString   = typeNode.asText ();

                switch (eventTypeString)
                    {
		    case "StatusUpdate":
			log.debug ("Parsed as StatusUpdate message {}", incomingString);
			appiEvent = objectMapper.readValue (incomingString, StatusUpdate.class);
			break;
		    // case "AuthRequest":
		    // 	appiEvent = objectMapper.readValue (incomingString, AuthRequest.class);
		    // 	break;
		    case "InfoRequest":
			log.debug ("Parsed as InfoRequest message {}", incomingString);
			appiEvent = objectMapper.readValue (incomingString, InfoRequest.class);
			break;
		    case "ReachabilityRequest":
			log.debug ("Parsed as ReachabilityRequest message {}", incomingString);
			appiEvent = objectMapper.readValue (incomingString, ReachabilityRequest.class);
			break;
		    case "GoingToBackground":
			log.debug ("Parsed as GoingToBackground message {}", incomingString);
			appiEvent = objectMapper.readValue (incomingString, GoingToBackground.class);
			break;
		    case "GoingToForeground":
			appiEvent = objectMapper.readValue (incomingString, GoingToForeground.class);
			break;
		    case "ContactAddRequest":
			appiEvent = objectMapper.readValue (incomingString, ContactAddRequest.class);
			break;
		    case "CurrentCallRequest":
			appiEvent = objectMapper.readValue (incomingString, CurrentCallRequest.class);
			break;			
		    default:
			log.error ("Unable to parse received message as an APPI JSON message {}", incomingString);
			messageJSONReceived = false;
		    }
	    }
        catch (IOException e)
            {
		log.error ("The incoming message could not be parsed as a {} message", eventTypeString);
                messageJSONReceived = false;
            }

        if (!messageJSONReceived)
            {
                throw new DecodeException (incomingString,
                                           "The incoming event WebSocket message could not be parsed as an APPIEvent object");
            }
        return appiEvent;
    }

    @Override
	public boolean willDecode (String incomingMessage)
    {
	return true;
    }
}
