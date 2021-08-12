/*
 * Java code generated by beanGenerator.py
 * 01.22.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * All of the current reachabilities of a specific user.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserReachability
{
	private String                   userID; // The userID of the user whose reachability information is being sent from the server.
	private List<HandleReachability> reachabilities; // The list of this user's handle reachability information.

    private static final ObjectMapper objectMapper = new ObjectMapper ();

	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public UserReachability ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public UserReachability (String _userID, List<HandleReachability> _reachabilities)
	{
		this.userID         = _userID;
		this.reachabilities = _reachabilities;
	}
	/*
	 * Setters
	 */
	public void setUserID (String _userID)
	{
		this.userID = _userID;
	}


	public void setReachabilities (List<HandleReachability> _reachabilities)
	{
		this.reachabilities = _reachabilities;
	}


	/*
	 * Getters
	 */
	public String getUserID ()
	{
		return this.userID;
	}


	public List<HandleReachability> getReachabilities ()
	{
		return this.reachabilities;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{

		String list0Str="[";
		for (HandleReachability var: reachabilities) 
		{
			list0Str += var.toString () + ", ";
		}
		list0Str += "]";
		return "userID: " + userID+ " " + "reachabilities: " + list0Str;
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