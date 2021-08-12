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
 * The Mailboxes ARI API. See https://wiki.asterisk.org/wiki/display/AST/Asterisk+13+Mailboxes+REST+API for details.
 */

import com.sun.jersey.api.client.ClientResponse;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailboxesARIAPI
{
	// Logging
	private static final Logger log = LoggerFactory.getLogger (MailboxesARIAPI.class.getName ());
	/**
	 *
	 * list: List all mailboxes.
	 *
	*/
	public static ClientResponse list ()
	{
		String RESTURLToUse = "/mailboxes";

		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 200)
		{
			log.debug ("No errors caught in call to /mailboxes");
		}
		return httpResponse;
	}

	/**
	 *
	 * get: Retrieve the current state of a mailbox.
	 *
	 *
	 * Path parameters:
	 * @param mailboxName: String: Name of the mailbox
	 *
	 * Errors caught:
	 * HTTP Error code 404: Mailbox not found
	*/
	public static ClientResponse get (String mailboxName)
	{
		String RESTURLToUse = "/mailboxes/{mailboxName}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{mailboxName}", mailboxName);
		ClientResponse httpResponse = HTTPAPIForARI.get (RESTURLToUse);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Mailbox not found");
		}
		return httpResponse;
	}

	/**
	 *
	 * update: Change the state of a mailbox. (Note:implicitly creates the mailbox).
	 *
	 *
	 * Query parameters:
	 * @param oldMessages: int: (required) Count of old messages in the mailbox
	 * @param newMessages: int: (required) Count of new messages in the mailbox
	 *
	 * Path parameters:
	 * @param mailboxName: String: Name of the mailbox
	 *
	 * Errors caught:
	 * HTTP Error code 404: Mailbox not found
	*/
	public static ClientResponse update (String mailboxName, String query)
	{
		String RESTURLToUse = "/mailboxes/{mailboxName}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{mailboxName}", mailboxName);
		ClientResponse httpResponse = HTTPAPIForARI.put (RESTURLToUse, query);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Mailbox not found");
		}
		return httpResponse;
	}

	/**
	 *
	 * delete: Destroy a mailbox.
	 *
	 *
	 * Path parameters:
	 * @param mailboxName: String: Name of the mailbox
	 *
	 * Errors caught:
	 * HTTP Error code 404: Mailbox not found
	*/
	public static ClientResponse delete (String mailboxName)
	{
		String RESTURLToUse = "/mailboxes/{mailboxName}";

		// Correct URI using path params
		RESTURLToUse = RESTURLToUse.replace ("{mailboxName}", mailboxName);
		ClientResponse httpResponse = HTTPAPIForARI.delete (RESTURLToUse);
		if (httpResponse.getStatus () == 404)
		{
			log.error ("Mailbox not found");
		}
		return httpResponse;
	}

}