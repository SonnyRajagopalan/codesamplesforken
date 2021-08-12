/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Java code generated by apiGenerator.py
 * 10.17.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The Endpoints ARI API. See https://wiki.asterisk.org/wiki/display/AST/Asterisk+13+Endpoints+REST+API for details.
 */

import com.sun.jersey.api.client.ClientResponse;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointsARIAPI
{
	// Logging
	private static final Logger log = LoggerFactory.getLogger (EndpointsARIAPI.class.getName ());
	/**
	 *
	 * list: List all endpoints.
	 *
	*/
	public static ClientResponse list ()
	{
		String RESTURLToUse = "/endpoints";

		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 200)
		{
			log.debug ("No errors caught in call to /endpoints");
		}
		return httpResponse;
	}

	/**
	 *
	 * sendMessage: Send a message to some technology URI or endpoint.
	 *
	 *
	 * Query parameters:
	 * @param to: String: (required) The endpoint resource or technology specific URI to send the message to. Valid resources are sip, pjsip, and xmpp.
	 * @param from: String: (required) The endpoint resource or technology specific identity to send this message from. Valid resources are sip, pjsip, and xmpp.
	 * @param body: String: The body of the message
	 *
	 * Body parameters:
	 * @param variables: containers: TBD
	 *
	 * Errors caught:
	 * HTTP Error code 400: Invalid parameters for sending a message.
	 * HTTP Error code 404: Endpoint not found
	*/
    public static ClientResponse sendMessage (String query, String body)
	{
		String RESTURLToUse = "/endpoints/sendMessage";

		ClientResponse httpResponse = null; //HTTPAPIForARI.put (RESTURLToUse, query);

		if (body.equals (""))
		    {
			httpResponse = HTTPAPIForARI.put (RESTURLToUse, query);
		    }
		else
		    {
			httpResponse = HTTPAPIForARI.put (RESTURLToUse, query, body);
		    }

		if (httpResponse.getStatus () == 400)
		{
			log.error ("Invalid parameters for sending a message.");
		}
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Endpoint not found");
		}
		return httpResponse;
	}

	/**
	 *
	 * listByTech: List available endoints for a given endpoint technology.
	 *
	 *
	 * Path parameters:
	 * @param tech: String: Technology of the endpoints (sip,iax2,...)
	 *
	 * Errors caught:
	 * HTTP Error code 404: Endpoints not found
	*/
	public static ClientResponse listByTech (String tech)
	{
		String RESTURLToUse = "/endpoints/{tech}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{tech}", tech);
		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Endpoints not found");
		}
		return httpResponse;
	}

	/**
	 *
	 * get: Details for an endpoint.
	 *
	 *
	 * Path parameters:
	 * @param tech: String: Technology of the endpoint
	 * @param resource: String: ID of the endpoint
	 *
	 * Errors caught:
	 * HTTP Error code 400: Invalid parameters for sending a message.
	 * HTTP Error code 404: Endpoints not found
	*/
	public static ClientResponse get (String tech, String resource)
	{
		String RESTURLToUse = "/endpoints/{tech}/{resource}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{tech}", tech);
		RESTURLToUse = RESTURLToUse.replace ("{resource}", resource);
		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 400)
		{
			log.error ("Invalid parameters for sending a message.");
		}
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Endpoints not found");
		}
		return httpResponse;
	}

	/**
	 *
	 * sendMessageToEndpoint: Send a message to some endpoint in a technology.
	 *
	 *
	 * Query parameters:
	 * @param from: String: (required) The endpoint resource or technology specific identity to send this message from. Valid resources are sip, pjsip, and xmpp.
	 * @param body: String: The body of the message
	 *
	 * Body parameters:
	 * @param variables: containers: TBD
	 *
	 * Path parameters:
	 * @param tech: String: Technology of the endpoint
	 * @param resource: String: ID of the endpoint
	 *
	 * Errors caught:
	 * HTTP Error code 400: Invalid parameters for sending a message.
	 * HTTP Error code 404: Endpoint not found
	*/
    public static ClientResponse sendMessageToEndpoint (String tech, String resource, String query, String body)
	{
		String RESTURLToUse = "/endpoints/{tech}/{resource}/sendMessage";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{tech}", tech);
		RESTURLToUse = RESTURLToUse.replace ("{resource}", resource);
		ClientResponse httpResponse = null; //HTTPAPIForARI.put (RESTURLToUse, query);

		if (body.equals (""))
		    {
			httpResponse = HTTPAPIForARI.put (RESTURLToUse, query);
		    }
		else
		    {
			httpResponse = HTTPAPIForARI.put (RESTURLToUse, query, body);
		    }

		if (httpResponse.getStatus () == 400)
		{
			log.error ("Invalid parameters for sending a message.");
		}
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Endpoint not found");
		}
		return httpResponse;
	}

}