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
 * Info about Asterisk status
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class StatusInfo
{
	private Timestamp last_reload_time; // Time when Asterisk was last reloaded.
	private Timestamp startup_time; // Time when Asterisk was started.


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public StatusInfo ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public StatusInfo (Timestamp _last_reload_time, Timestamp _startup_time)
	{
		this.last_reload_time = _last_reload_time;
		this.startup_time     = _startup_time;
	}
	/*
	 * Setters
	 */
	public void setLast_reload_time (Timestamp _last_reload_time)
	{
		this.last_reload_time = _last_reload_time;
	}


	public void setStartup_time (Timestamp _startup_time)
	{
		this.startup_time = _startup_time;
	}


	/*
	 * Getters
	 */
	public Timestamp getLast_reload_time ()
	{
		return this.last_reload_time;
	}


	public Timestamp getStartup_time ()
	{
		return this.startup_time;
	}


}
