/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.ari;

import javax.xml.bind.DatatypeConverter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPAPIForARI
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (HTTPAPIForARI.class.getName ());

    private static String base;
    private static String username;
    private static String password;

    public HTTPAPIForARI (String _base, String _username, String _password)
    {
	log.debug ("HTTPAPIForARI class initialized with base {}, username {} and password {}", 
		   _base, _username, _password);

	base     = _base;
	username = _username;
	password = _password;
    }    
    
    public static String getBase ()
    {
	return base;
    }

    public static String getUsername ()
    {
	return username;
    }

    public static String getPassword ()
    {
	return password;
    }

    public static String getBasicAuthEncoded ()
    {
	String authString = getUsername () + ":" + getPassword ();
	return DatatypeConverter.printBase64Binary (authString.getBytes ());
    }

    public static ClientResponse get (String path, String query)
    {
	return get (path + query);
    }

    public static ClientResponse get (String path)
    {
	Client client = Client.create();
	
	WebResource ariResource = client.resource(getBase () + path);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").get(ClientResponse.class);
    }

    public static ClientResponse put (String path, String query)
    {
	return put (path + query);
    }

    public static ClientResponse put (String path)
    {
	Client client = Client.create();
	
	WebResource ariResource = client.resource(getBase () + path);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").put(ClientResponse.class);
    }

    public static ClientResponse put (String path, String query, String body)
    {
	Client client = Client.create();
	
	WebResource ariResource = client.resource(getBase () + path);

	if (body.equals (""))
	    {
		return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
		    accept("application/json").put(ClientResponse.class);
	    }
	else
	    {
		return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
		    accept("application/json").put(ClientResponse.class, body);
	    }

    }

    public static ClientResponse post (String path)
    {
	Client client = Client.create();
	WebResource ariResource = client.resource(getBase () + path);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").post(ClientResponse.class);
    }

    public static ClientResponse post (String path, String query)
    {
	Client client = Client.create();
	WebResource ariResource = client.resource(getBase () + path + query);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").post(ClientResponse.class);
    }

    public static ClientResponse post (String path, String query, String body)
    {
	Client client = Client.create();
	WebResource ariResource = client.resource(getBase () + path + query);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").post(ClientResponse.class, body);
    }

    public static ClientResponse delete (String path, String query)
    {
	return delete (path + query);
    }

    public static ClientResponse delete (String path)
    {
	Client client = Client.create();
	
	WebResource ariResource = client.resource(getBase () + path);

	return ariResource.header ("Authorization", "Basic " + getBasicAuthEncoded ()).
	    accept("application/json").delete(ClientResponse.class);
    }
}
