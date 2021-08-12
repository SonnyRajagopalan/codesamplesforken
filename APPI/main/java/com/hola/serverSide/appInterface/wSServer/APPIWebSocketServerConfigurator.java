package com.hola.serverSide.appInterface.wSServer;

import java.util.Base64;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.lang.IllegalArgumentException;
/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Hola specific
 */
import com.hola.serverSide.appInterface.peer.APPIPeerState;
import com.hola.serverSide.appInterface.peer.APPIPeer;
import com.hola.serverSide.appInterface.hsm.HSMEvent;
import com.hola.serverSide.appInterface.stormpath.StormpathClientSingleton;
//import org.eclipse.jetty.websocket.jsr356.server.BasicServerEndpointConfig;
//import org.eclipse.jetty.websocket.jsr356.server.JsrHandshakeRequest;

public class APPIWebSocketServerConfigurator extends ServerEndpointConfig.Configurator
{
    private static final Logger log = LoggerFactory.getLogger (APPIWebSocketServerConfigurator.class.getName ());

    public static final String HTTP_HEADERS_FROM_CLIENT_REQUEST = "HTTP_HEADERS_FROM_CLIENT_REQUEST";
    public static final String PEER_STATE                       = "PEER_STATE";
    public static final String HTTP_SESSION                     = "HTTP_SESSION";

    @Override
    public void modifyHandshake (ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response)
    {
	/*
	  HandShakeRequest headers 
	  {Authorization=[QmFzaWMgS2Vhcnk6SDAxNEQzbTA2MDA4UDQkJHcwcmQ=], 
	  Connection=[Upgrade], 
	  Host=[192.168.1.102], 
	  Origin=[192.168.1.102], 
	  Sec-WebSocket-Key=[3Rc0Pj7FOr0tdel4R3wUSQ==], 
	  Sec-WebSocket-Version=[13], 
	  Upgrade=[websocket]}
	  HandshakeResponse headers {}
	 */
	StormpathClientSingleton stormpathClientSingleton = StormpathClientSingleton.getInstance ();
	//APPIPeerState peerState = new APPIPeerState (new APPIPeer (), null, HSMEvent.ReceivedAuthRequest);
	//BasicServerEndpointConfig basicServerEndpointConfig = (BasicServerEndpointConfig) sec;
	//JsrHandshakeRequest jsrHandshakeRequest = (JsrHandshakeRequest) request;
	APPIPeerState peerState = new APPIPeerState ();
	String usernameOrEmail, password;

	Map <String, List <String>> httpHeaders = request.getHeaders ();
	sec.getUserProperties ().put (HTTP_HEADERS_FROM_CLIENT_REQUEST, httpHeaders);
	sec.getUserProperties ().put (PEER_STATE, peerState); // This is borderline obscene to do, but what the heck!
	//log.debug ("HandshakeRequest  headers {}", jsrHandshakeRequest.getHttpSession ().toString ());
	//log.debug ("HandshakeResponse headers {}", response.getHeaders ());
	//log.debug ("ServerEndpointConfig {}", basicServerEndpointConfig.toString ());

	String informationToAuthenticateWith = getAuthorizationInfo (httpHeaders);
	int indexOfColon = informationToAuthenticateWith.indexOf (":");
	usernameOrEmail  = informationToAuthenticateWith.substring (0, indexOfColon);
	password         = informationToAuthenticateWith.substring (indexOfColon + 1);
	log.debug ("Got this auth info {}:{}", usernameOrEmail, password);
	
	//if (stormpathClientSingleton.authenticateAUserWithStormpath ("srajagopalan@pacifi.net", "H014D3m0"))
	if (stormpathClientSingleton.authenticateAUserWithStormpath (usernameOrEmail, password))
	    {
		log.debug ("Authentication with Stormpath auth worked for {}!", usernameOrEmail);
		// Regardless of what they use to login, use lowercase only
		peerState.getPeer ().setUsernameOrEmail (usernameOrEmail.toLowerCase ());
	    }
	else
	    {
		log.debug ("Stormpath auth did not work for {}!", usernameOrEmail);
		APPIPeerState.removeAPeerFromDB (peerState.getUUID ());
		throw new IllegalArgumentException ();
	    }	
    }

    private String getAuthorizationInfo (Map <String, List <String>> httpHeaders)
    {
	byte [] authBytes = null;

	for (Map.Entry <String, List <String>> oneEntry : httpHeaders.entrySet ())
	    {
		for (String oneValueInOneHeader : oneEntry.getValue ())
		    {
			//headerInfo += "Key = " + oneEntry.getKey () + ", Value = " + oneValueInOneHeader + "\n";
			if (oneEntry.getKey ().equals ("Authorization"))
			    {
				try
				    {
					// See https://en.wikipedia.org/wiki/Basic_access_authentication
					String authString = oneValueInOneHeader.substring (6); // Discard "Basic "
					authBytes = Base64.getDecoder ().decode (authString);
				    }
				catch (IllegalArgumentException e)
				    {
					e.printStackTrace ();
				    }
				// finally
				//     {
				// 	authRequest.setAuthorizationInfo(new String (authBytes));
				//     }
			    }
		    }
	    }

	return new String (authBytes);
    }
}
