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
 * The Playbacks ARI API. See https://wiki.asterisk.org/wiki/display/AST/Asterisk+13+Playbacks+REST+API for details.
 */

import com.sun.jersey.api.client.ClientResponse;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaybacksARIAPI
{
	// Logging
	private static final Logger log = LoggerFactory.getLogger (PlaybacksARIAPI.class.getName ());
	/**
	 *
	 * get: Get a playback's details.
	 *
	 *
	 * Path parameters:
	 * @param playbackId: String: Playback's id
	 *
	 * Errors caught:
	 * HTTP Error code 404: The playback cannot be found
	*/
	public static ClientResponse get (String playbackId)
	{
		String RESTURLToUse = "/playbacks/{playbackId}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{playbackId}", playbackId);
		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("The playback cannot be found");
		}
		return httpResponse;
	}

	/**
	 *
	 * stop: Stop a playback.
	 *
	 *
	 * Path parameters:
	 * @param playbackId: String: Playback's id
	 *
	 * Errors caught:
	 * HTTP Error code 404: The playback cannot be found
	*/
	public static ClientResponse stop (String playbackId)
	{
		String RESTURLToUse = "/playbacks/{playbackId}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{playbackId}", playbackId);
		ClientResponse httpResponse = HTTPAPIForARI.delete (RESTURLToUse);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("The playback cannot be found");
		}
		return httpResponse;
	}

	/**
	 *
	 * control: Control a playback.
	 *
	 *
	 * Query parameters:
	 * @param operation: String: (Required) Operation to perform on the playback. Allowed values
	 *
	 * Path parameters:
	 * @param playbackId: String: Playback's id
	 *
	 * Errors caught:
	 * HTTP Error code 400: The provided operation parameter was invalid
	 * HTTP Error code 404: The playback cannot be found
	 * HTTP Error code 409: The operation cannot be performed in the playback's current state
	*/
	public static ClientResponse control (String playbackId, String query)
	{
		String RESTURLToUse = "/playbacks/{playbackId}/control";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{playbackId}", playbackId);
		ClientResponse httpResponse = HTTPAPIForARI.post (RESTURLToUse, query);
		if (httpResponse.getStatus () == 400)
		{
			log.error ("The provided operation parameter was invalid");
		}
		if (httpResponse.getStatus () == 404)
		{
			log.error ("The playback cannot be found");
		}
		if (httpResponse.getStatus () == 409)
		{
			log.error ("The operation cannot be performed in the playback's current state");
		}
		return httpResponse;
	}

}
