/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
/*
 * Java code autogenerated by beanGenerator.py
 * 10.06.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * A specific communication connection between Asterisk and an Endpoint.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class Channel
{
	private String      accountcode; // Account code for channel
	private CallerID    caller; // Caller ID for caller
	private CallerID    connected; // Caller ID of connected user
	private Timestamp   creationtime; // Timestamp when channel was created
	private DialplanCEP dialplan; // Current location in the dialplan
	private String      id; // Unique identifier of the channel.
	private String      language; // The default spoken language
	private String      name; // Name of the channel (i.e. SIP/foo
	private String      state; // State of channel


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public Channel ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public Channel (String _accountcode, CallerID _caller, CallerID _connected, Timestamp _creationtime, DialplanCEP _dialplan, String _id, String _language, String _name, String _state)
	{
		this.accountcode  = _accountcode;
		this.caller       = _caller;
		this.connected    = _connected;
		this.creationtime = _creationtime;
		this.dialplan     = _dialplan;
		this.id           = _id;
		this.language     = _language;
		this.name         = _name;
		this.state        = _state;
	}
	/*
	 * Setters
	 */
	public void setAccountcode (String _accountcode)
	{
		this.accountcode = _accountcode;
	}


	public void setCaller (CallerID _caller)
	{
		this.caller = _caller;
	}


	public void setConnected (CallerID _connected)
	{
		this.connected = _connected;
	}


	public void setCreationtime (Timestamp _creationtime)
	{
		this.creationtime = _creationtime;
	}


	public void setDialplan (DialplanCEP _dialplan)
	{
		this.dialplan = _dialplan;
	}


	public void setId (String _id)
	{
		this.id = _id;
	}


	public void setLanguage (String _language)
	{
		this.language = _language;
	}


	public void setName (String _name)
	{
		this.name = _name;
	}


	public void setState (String _state)
	{
		this.state = _state;
	}


	/*
	 * Getters
	 */
	public String getAccountcode ()
	{
		return this.accountcode;
	}


	public CallerID getCaller ()
	{
		return this.caller;
	}


	public CallerID getConnected ()
	{
		return this.connected;
	}


	public Timestamp getCreationtime ()
	{
		return this.creationtime;
	}


	public DialplanCEP getDialplan ()
	{
		return this.dialplan;
	}


	public String getId ()
	{
		return this.id;
	}


	public String getLanguage ()
	{
		return this.language;
	}


	public String getName ()
	{
		return this.name;
	}


	public String getState ()
	{
		return this.state;
	}


}