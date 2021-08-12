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
 * (Derived from Event object) Notification that a bridge has been destroyed.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class BridgeDestroyed extends Event
{
	private Bridge    bridge; // Bridge that was destroyed


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public BridgeDestroyed ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public BridgeDestroyed (String _type, String _application, Timestamp _timestamp, Bridge _bridge)
	{
		super (_type, _application, _timestamp);
		this.bridge      = _bridge;
	}
	/*
	 * Setters
	 */
	public void setBridge (Bridge _bridge)
	{
		this.bridge = _bridge;
	}


	/*
	 * Getters
	 */
	public Bridge getBridge ()
	{
		return this.bridge;
	}


}
