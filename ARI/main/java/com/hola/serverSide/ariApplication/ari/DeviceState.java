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
 * Represents the state of a device.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */

public class DeviceState
{
	private String name; // Name of the device.
	private String state; // Device's state


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public DeviceState ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public DeviceState (String _name, String _state)
	{
		this.name  = _name;
		this.state = _state;
	}
	/*
	 * Setters
	 */
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
	public String getName ()
	{
		return this.name;
	}


	public String getState ()
	{
		return this.state;
	}


}
