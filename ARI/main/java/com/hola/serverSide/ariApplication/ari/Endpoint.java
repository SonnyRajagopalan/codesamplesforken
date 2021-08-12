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
 * An external device that may offer/accept calls to/from Asterisk. Unlike most resources, which have a single unique identifier, an endpoint is uniquely identified by the technology/resource pair.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.util.ArrayList;
import java.util.List;

public class Endpoint
{
	private List<String> channel_ids; // Id's of channels associated with this endpoint
	private String       resource; // Identifier of the endpoint, specific to the given technology.
	private String       state; // (optional) Endpoint's state
	private String       technology; // Technology of the endpoint


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public Endpoint ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public Endpoint (List<String> _channel_ids, String _resource, String _state, String _technology)
	{
		this.channel_ids = _channel_ids;
		this.resource    = _resource;
		this.state       = _state;
		this.technology  = _technology;
	}
	/*
	 * Setters
	 */
	public void setChannel_ids (List<String> _channel_ids)
	{
		this.channel_ids = _channel_ids;
	}


	public void setResource (String _resource)
	{
		this.resource = _resource;
	}


	public void setState (String _state)
	{
		this.state = _state;
	}


	public void setTechnology (String _technology)
	{
		this.technology = _technology;
	}


	/*
	 * Getters
	 */
	public List<String> getChannel_ids ()
	{
		return this.channel_ids;
	}


	public String getResource ()
	{
		return this.resource;
	}


	public String getState ()
	{
		return this.state;
	}


	public String getTechnology ()
	{
		return this.technology;
	}


}