/*
 * Java code generated by beanGenerator.py
 * 02.12.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The current reachability of a specific handle.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HandleReachability
{
	private String app; // The app. E.g., Skype.
	private String handle; // The specific handle on the app. E.g., Sonny.Rajagopalan (Sonny's Skype handle).
	private String method; // The specific method of reachability on the app. Values "voice", "video", "text", "data"
	private String reachability; // The current reachability of the handle. Values "AVAILABLE"/"NOT_AVAILABLE" (for now).

    private static final ObjectMapper objectMapper = new ObjectMapper ();
	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public HandleReachability ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public HandleReachability (String _app, String _handle, String _method, String _reachability)
	{
		this.app          = _app;
		this.handle       = _handle;
		this.method       = _method;
		this.reachability = _reachability;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("app")
	public void setApp (String _app)
	{
		this.app = _app;
	}


	@JsonProperty ("handle")
	public void setHandle (String _handle)
	{
		this.handle = _handle;
	}


	@JsonProperty ("method")
	public void setMethod (String _method)
	{
		this.method = _method;
	}


	@JsonProperty ("reachability")
	public void setReachability (String _reachability)
	{
		this.reachability = _reachability;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("app")
	public String getApp ()
	{
		return this.app;
	}


	@JsonProperty ("handle")
	public String getHandle ()
	{
		return this.handle;
	}


	@JsonProperty ("method")
	public String getMethod ()
	{
		return this.method;
	}


	@JsonProperty ("reachability")
	public String getReachability ()
	{
		return this.reachability;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "app: " + app+ " " + "handle: " + handle+ " " + "method: " + method+ " " + "reachability: " + reachability;
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
