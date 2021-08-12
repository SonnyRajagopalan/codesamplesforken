/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.wSClient;


import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.ariApplication.ari.ARITransactionType;
import com.hola.serverSide.ariApplication.ari.EventDecoder;
import com.hola.serverSide.ariApplication.ari.Event;

@ClientEndpoint (decoders = {EventDecoder.class})
public class WSClient
{
        // Logging
    private static final Logger log = LoggerFactory.getLogger (WSClient.class.getName ());

    private WSClientWrapper context;
    private Session session;

    public WSClient (WSClientWrapper _context)
    {
	this.context = _context;
    }

    public Session getSession ()
    {
	return this.session;
    }

    @OnOpen
    public void open (Session _session)
    {
	log.debug ("A new session was created...");
	this.session = _session;
    }

    @OnMessage
    public void receivedSomething (Event ariEvent)
    {
	Runnable eventProcessor = new WSClientRunnable (ariEvent);
	Thread t = new Thread (eventProcessor);
	t.start ();
    }

    @OnClose
    public void closed (Session session)
    {
	log.debug ("Closed session " + session);
	//log.info ("Closed session");
    }

    @OnError
    public void error (Throwable error)
    {
	log.debug ("WSClient Error: " + error.getMessage ());
	//log.error ("Error {}", error.getMessage ());
    }
}
