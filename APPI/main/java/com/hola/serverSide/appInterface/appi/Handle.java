/*
 * Java code generated by beanGenerator.py
 * 02.12.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The handle information for an app.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Handle
{
	private String app; // The app, E.g., "Skype".
	private String device; // The device this app and handle work. E.g., "mobile" or "desktop".
	private String handle; // The specific handle on the app. E.g., Sonny.Rajagopalan (Sonny's Skype handle). NOTE
    private static final ObjectMapper objectMapper = new ObjectMapper ();

	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public Handle ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public Handle (String _app, String _device, String _handle)
	{
		this.app    = _app;
		this.device = _device;
		this.handle = _handle;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("app")
	public void setApp (String _app)
	{
		this.app = _app;
	}


	@JsonProperty ("device")
	public void setDevice (String _device)
	{
		this.device = _device;
	}


	@JsonProperty ("handle")
	public void setHandle (String _handle)
	{
		this.handle = _handle;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("app")
	public String getApp ()
	{
		return this.app;
	}


	@JsonProperty ("device")
	public String getDevice ()
	{
		return this.device;
	}


	@JsonProperty ("handle")
	public String getHandle ()
	{
		return this.handle;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "app: " + app+ " " + "device: " + device+ " " + "handle: " + handle;
	}
	/*
	 * get JSON string object
	 */
	public String toJsonString ()
	{
	    //ObjectMapper mapper = new ObjectMapper ();
		String thisBeanAsJson = null;
		try
		{
			thisBeanAsJson = objectMapper.writeValueAsString (this);
		}
		catch (JsonProcessingException e)
		{
			e.printStackTrace ();
		}
		finally
		{
			return thisBeanAsJson;
		}
	}
}
