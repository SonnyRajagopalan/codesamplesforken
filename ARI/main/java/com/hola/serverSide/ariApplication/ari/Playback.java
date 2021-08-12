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
 * Object representing the playback of media to a channel
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */

public class Playback
{
	private String id; // ID for this playback operation
	private String language; // (optional) For media types that support multiple languages, the language requested for playback.
	private String media_uri; // URI for the media to play back.
	private String state; // Current state of the playback operation.
	private String target_uri; // URI for the channel or bridge to play the media on


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public Playback ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public Playback (String _id, String _language, String _media_uri, String _state, String _target_uri)
	{
		this.id         = _id;
		this.language   = _language;
		this.media_uri  = _media_uri;
		this.state      = _state;
		this.target_uri = _target_uri;
	}
	/*
	 * Setters
	 */
	public void setId (String _id)
	{
		this.id = _id;
	}


	public void setLanguage (String _language)
	{
		this.language = _language;
	}


	public void setMedia_uri (String _media_uri)
	{
		this.media_uri = _media_uri;
	}


	public void setState (String _state)
	{
		this.state = _state;
	}


	public void setTarget_uri (String _target_uri)
	{
		this.target_uri = _target_uri;
	}


	/*
	 * Getters
	 */
	public String getId ()
	{
		return this.id;
	}


	public String getLanguage ()
	{
		return this.language;
	}


	public String getMedia_uri ()
	{
		return this.media_uri;
	}


	public String getState ()
	{
		return this.state;
	}


	public String getTarget_uri ()
	{
		return this.target_uri;
	}


}
