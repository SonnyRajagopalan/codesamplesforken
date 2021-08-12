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
 * Info about Asterisk
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */

public class SystemInfo
{
	private String entity_id; // Entity ID
	private String version; // Asterisk version.


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public SystemInfo ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public SystemInfo (String _entity_id, String _version)
	{
		this.entity_id = _entity_id;
		this.version   = _version;
	}
	/*
	 * Setters
	 */
	public void setEntity_id (String _entity_id)
	{
		this.entity_id = _entity_id;
	}


	public void setVersion (String _version)
	{
		this.version = _version;
	}


	/*
	 * Getters
	 */
	public String getEntity_id ()
	{
		return this.entity_id;
	}


	public String getVersion ()
	{
		return this.version;
	}


}
