/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.wSClient;

import java.lang.Exception;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSClientWrapper
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (WSClientWrapper.class.getName ());

    private WSClient wsClient;

    public WSClientWrapper (String URIString)
    {
	log.debug ("Constructor...");
	wsClient = new WSClient (this);
	
	this.connect (URIString);
	//this.sendToServer ();
    }
    private void connect (String URIString)
    {
	try
	    {
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer ();
		webSocketContainer.connectToServer (wsClient, new URI (URIString));
		log.debug ("Connected .... ");
	    }
	catch (IOException | URISyntaxException | DeploymentException e)
	    {
		e.printStackTrace ();
		//log.debug ("Error connecting to " + ARIWSServerURIString + " owing to [" + e.toString() + ")");
	    }
    }

    private void sendToServer ()
    {
	try
	    {
		log.debug ("Sending a silly message to the server....");
		wsClient.getSession ().getAsyncRemote ().sendText ("Silly message");
	    }
	catch (IllegalArgumentException ex)
	    {
		log.debug ("Crap!");
		//Logger.getLogger (WSClientWrapper.class.getName ()).log(Level.SEVERE, null, ex);
	    }

    }
    
    public static void main (String[] args)
    {
	log.debug ("Starting things up...");
	WSClientWrapper thisInstance = new WSClientWrapper ("ws://localhost:8088/ari/events?api_key=asterisk:asterisk&app=psa");
	while (1==1)
	    {
	    }
    }
}
