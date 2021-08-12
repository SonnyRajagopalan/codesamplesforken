package com.hola.serverSide.appInterface.wSServer;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.websocket.*;
import javax.websocket.server.*;
import javax.websocket.EndpointConfig;
import java.util.Base64;

import org.eclipse.jetty.websocket.jsr356.JsrSession;
import org.eclipse.jetty.websocket.common.WebSocketSession;
//import org.eclipse.jetty.websocket.server.WebSocketServerConnection;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hola.serverSide.appInterface.appi.APPIEventDecoder;
import com.hola.serverSide.appInterface.wSServer.APPIEventProcessorRunnable;
import com.hola.serverSide.appInterface.appi.APPIEvent;
import com.hola.serverSide.appInterface.appi.AuthRequest;
import com.hola.serverSide.appInterface.peer.APPIPeer;
import com.hola.serverSide.appInterface.peer.APPIPeerState;
import com.hola.serverSide.appInterface.hsm.HSMBaseState;
import com.hola.serverSide.appInterface.hsm.HSMEvent;
import com.hola.serverSide.appInterface.hsm.HSMState;
import com.hola.serverSide.appInterface.common.ThreadUtils;
import com.hola.serverSide.appInterface.logging.PeerEventLogger;

@ServerEndpoint(value="/v_0/appi", configurator = APPIWebSocketServerConfigurator.class, decoders={APPIEventDecoder.class})
//@ServerEndpoint(value="/v_0/appi", decoders={APPIMessageDecoder.class})
public class APPIWebSocketServer 
{
    private static final Logger log = LoggerFactory.getLogger (APPIWebSocketServer.class.getName ());

    public static final int MESSAGE_MAX =  15 * 1000 * 1024;   // 15, 000 kb
    
    @OnOpen
    public void open (Session session, EndpointConfig config) 
    {
	/*
	  Removed when we moved the auth processing into modifyHandshake ()
	  
	  APPIPeerState peerState = new APPIPeerState (new APPIPeer (), session, HSMEvent.ReceivedAuthRequest);
	  AuthRequest authRequest = new AuthRequest (session, config, "ToBeSetLater");
	  Runnable appiEventProcessor = new APPIEventProcessorRunnable (peerState, authRequest);
	  Thread t = new Thread (appiEventProcessor);
	  t.start ();
	*/
	APPIPeerState peerState = (APPIPeerState) config.getUserProperties ().get ("PEER_STATE");
	peerState.setSession (session);
	JsrSession jsrSession = (JsrSession) session;
	WebSocketSession webSocketSession = jsrSession.getWebSocketSession ();
	peerState.getPeer ().setIpAddress (webSocketSession.getRemoteAddress ().getHostString ());
	peerState.changeState (HSMBaseState.getStateForStateName (HSMState.ActiveHAPPI));
	PeerEventLogger.peerLog (peerState, "Created for " + peerState.getPeer ().getUsernameOrEmail () + 
			 " at IP address: " + peerState.getPeer ().getIpAddress ());
	log.debug ("maxIdleTimeout = {}", session.getMaxIdleTimeout ());
	// final Session thisSession = session;
	// thisSession.addMessageHandler (new MessageHandler.Whole <String> ()
	// 			       {
	// 				   @Override
	// 				   public void onMessage (String text)
	// 				   {	
	// 				       Runnable appiEventProcessor = new 
	// 					   APPIEventProcessorRunnable (thisPeerState, appiEvent);
	// 				       Thread t = new Thread (appiEventProcessor);
	// 				       t.start ();
	// 				       log.debug ("Received an event of type " + appiEvent.getType ());
	// 				   }
	// 			       });
    }    
    
    @OnMessage
    public void receivedSomething (Session session, APPIEvent appiEvent)
    {
	Runnable appiEventProcessor = new APPIEventProcessorRunnable (APPIPeerState.getPeerStateFromSession (session), 
								      appiEvent);
	Thread t = new Thread (appiEventProcessor);
	t.start ();
	log.debug ("Received an event of type " + appiEvent.getType ());
    }

    public void reportMessage (Session s, String message) 
    {
        try 
	    {
		String timeStamp = DateFormat.getTimeInstance().format(new Date());
		s.getBasicRemote().sendText(timeStamp + " " + message);
	    } 
	catch (IOException ioe) 
	    {
		//System.out.println(ioe.getMessage());
	    }
    }
    
    @OnError
    public void error (Throwable t) 
    {
	log.error ("Error owing to {}", t.getMessage ());
	log.error (ThreadUtils.getThreadIDedStackTraceString ());
	/*
	  What happens when an error is triggered, is a websocket connection closed?
	  APPIPeerState.removeAPeerFromDB (UUID);
	*/
    }
    
    @OnClose
    public void close (Session s, CloseReason cr) 
    {
	log.debug ("Server closing because of {}", cr.getReasonPhrase ());
	PeerEventLogger.peerLog (APPIPeerState.getPeerStateFromSession (s), "Closing for " + cr.getReasonPhrase ());
	String UUID = APPIPeerState.getPeerStateUUIDFromSession (s);
	if (UUID != null)
	    {
		log.debug ("Removing peer UUID {} from DB for normal close", UUID);
		PeerEventLogger.peerLog (APPIPeerState.getPeerStateFromSession (s), "Removing from DB");

		APPIPeerState.removeAPeerFromDB (UUID);
	    }
	else
	    {
		log.error ("Cannot find APPIPeerState with session ID {}", s.getId ());
	    }
    }
     
}
