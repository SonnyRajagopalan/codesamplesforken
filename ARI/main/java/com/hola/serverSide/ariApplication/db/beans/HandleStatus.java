/*
 * Java code generated by beanGenerator.py
 * 02.12.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The current status of a specific handle.
 */
package com.hola.serverSide.ariApplication.db.beans;
/*
 * Import statements 
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HandleStatus
{
	private String  app; // The app, E.g., "Skype".
	private String  method; // The specific method of reachability on the app. Values "voice", "video", "text", "data"
	private String  handle; // The specific handle on the app. E.g., Sonny.Rajagopalan (Sonny's Skype handle). NOTE
	private boolean status; // The current status of the handle. Values True/False (boolean) (for now).

    private static final ObjectMapper objectMapper = new ObjectMapper ();
	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public HandleStatus ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public HandleStatus (String _app, String _method, String _handle, boolean _status)
	{
		this.app    = _app;
		this.method = _method;
		this.handle = _handle;
		this.status = _status;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("app")
	public void setApp (String _app)
	{
		this.app = _app;
	}


	@JsonProperty ("method")
	public void setMethod (String _method)
	{
		this.method = _method;
	}


	@JsonProperty ("handle")
	public void setHandle (String _handle)
	{
		this.handle = _handle;
	}


	@JsonProperty ("status")
	public void setStatus (boolean _status)
	{
		this.status = _status;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("app")
	public String getApp ()
	{
		return this.app;
	}


	@JsonProperty ("method")
	public String getMethod ()
	{
		return this.method;
	}


	@JsonProperty ("handle")
	public String getHandle ()
	{
		return this.handle;
	}


	@JsonProperty ("status")
	public boolean getStatus ()
	{
		return this.status;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "app: " + app+ " " + "method: " + method+ " " + "handle: " + handle+ " " + "status: " + status;
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
