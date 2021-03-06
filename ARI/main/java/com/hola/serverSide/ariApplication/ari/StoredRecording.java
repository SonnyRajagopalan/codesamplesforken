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
 * A past recording that may be played back.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */

public class StoredRecording
{
	private String format; // Format of recording
	private String name; // Name of recording


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public StoredRecording ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public StoredRecording (String _format, String _name)
	{
		this.format = _format;
		this.name   = _name;
	}
	/*
	 * Setters
	 */
	public void setFormat (String _format)
	{
		this.format = _format;
	}


	public void setName (String _name)
	{
		this.name = _name;
	}


	/*
	 * Getters
	 */
	public String getFormat ()
	{
		return this.format;
	}


	public String getName ()
	{
		return this.name;
	}


}
