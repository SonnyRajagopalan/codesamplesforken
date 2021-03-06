/*
 * Java code generated by beanGenerator.py
 * 02.20.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The class for describing a currently active handover.
 */
package com.hola.serverSide.ariApplication.db.beans;
/*
 * Import statements 
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveHandover
{
	private String UUID; // The UUID of the handover requesting call.
	private int    extension; // The integer extension number that pertains to the user who is requesting the handover.
	private String callerID; // The caller ID for the handover requesting call.
	private String callerHandle; // The currently used caller handle for the call.
	private String callerChannel; // The caller channel for the handover requesting call.
	private String callerOrCallee; // Whether or not this is the caller or the callee from the original call (in the UUID) requesting this handover.
	private static final ObjectMapper objectMapper = new ObjectMapper ();



	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public ActiveHandover ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public ActiveHandover (String _UUID, int _extension, String _callerID, String _callerHandle, String _callerChannel, String _callerOrCallee)
	{
		this.UUID           = _UUID;
		this.extension      = _extension;
		this.callerID       = _callerID;
		this.callerHandle   = _callerHandle;
		this.callerChannel  = _callerChannel;
		this.callerOrCallee = _callerOrCallee;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("UUID")
	public void setUUID (String _UUID)
	{
		this.UUID = _UUID;
	}


	@JsonProperty ("extension")
	public void setExtension (int _extension)
	{
		this.extension = _extension;
	}


	@JsonProperty ("callerID")
	public void setCallerID (String _callerID)
	{
		this.callerID = _callerID;
	}


	@JsonProperty ("callerHandle")
	public void setCallerHandle (String _callerHandle)
	{
		this.callerHandle = _callerHandle;
	}


	@JsonProperty ("callerChannel")
	public void setCallerChannel (String _callerChannel)
	{
		this.callerChannel = _callerChannel;
	}


	@JsonProperty ("callerOrCallee")
	public void setCallerOrCallee (String _callerOrCallee)
	{
		this.callerOrCallee = _callerOrCallee;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("UUID")
	public String getUUID ()
	{
		return this.UUID;
	}


	@JsonProperty ("extension")
	public int getExtension ()
	{
		return this.extension;
	}


	@JsonProperty ("callerID")
	public String getCallerID ()
	{
		return this.callerID;
	}


	@JsonProperty ("callerHandle")
	public String getCallerHandle ()
	{
		return this.callerHandle;
	}


	@JsonProperty ("callerChannel")
	public String getCallerChannel ()
	{
		return this.callerChannel;
	}


	@JsonProperty ("callerOrCallee")
	public String getCallerOrCallee ()
	{
		return this.callerOrCallee;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "UUID: " + UUID+ " " + "extension: " + extension+ " " + "callerID: " + callerID+ " " + "callerHandle: " + callerHandle+ " " + "callerChannel: " + callerChannel+ " " + "callerOrCallee: " + callerOrCallee;
	}
	/*
	 * get JSON string object
	 */
	public String toJsonString ()
	{
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
