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
 * (Derived from Event object) Notification that a channel has been destroyed.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class ChannelDestroyed extends Event
{
	private int       cause; // Integer representation of the cause of the hangup
	private String    cause_txt; // Text representation of the cause of the hangup
	private Channel   channel; // Channel that was destroyed


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public ChannelDestroyed ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public ChannelDestroyed (String _type, String _application, Timestamp _timestamp, int _cause, String _cause_txt, Channel _channel)
	{
		super (_type, _application, _timestamp);
		this.cause       = _cause;
		this.cause_txt   = _cause_txt;
		this.channel     = _channel;
	}
	/*
	 * Setters
	 */
	public void setCause (int _cause)
	{
		this.cause = _cause;
	}


	public void setCause_txt (String _cause_txt)
	{
		this.cause_txt = _cause_txt;
	}


	public void setChannel (Channel _channel)
	{
		this.channel = _channel;
	}


	/*
	 * Getters
	 */
	public int getCause ()
	{
		return this.cause;
	}


	public String getCause_txt ()
	{
		return this.cause_txt;
	}


	public Channel getChannel ()
	{
		return this.channel;
	}


}