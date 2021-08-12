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
 * (Derived from Event object)A hangup was requested on the channel.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class ChannelHangupRequest extends Event
{
	private int       cause; // (optional) Integer representation of the cause of the hangup.
	private Channel   channel; // The channel on which the hangup was requested.
	private boolean   soft; // (optional) Whether the hangup request was a soft hangup request.


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public ChannelHangupRequest ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public ChannelHangupRequest (String _type, String _application, Timestamp _timestamp, int _cause, Channel _channel, boolean _soft)
	{
		super (_type, _application, _timestamp);
		this.cause       = _cause;
		this.channel     = _channel;
		this.soft        = _soft;
	}
	/*
	 * Setters
	 */
	public void setCause (int _cause)
	{
		this.cause = _cause;
	}


	public void setChannel (Channel _channel)
	{
		this.channel = _channel;
	}


	public void setSoft (boolean _soft)
	{
		this.soft = _soft;
	}


	/*
	 * Getters
	 */
	public int getCause ()
	{
		return this.cause;
	}


	public Channel getChannel ()
	{
		return this.channel;
	}


	public boolean getSoft ()
	{
		return this.soft;
	}


}
