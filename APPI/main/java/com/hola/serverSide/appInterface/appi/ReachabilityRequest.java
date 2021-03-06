/*
 * Java code generated by beanGenerator.py
 * 02.12.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * (Derived from APPIEvent object) Reachability request from the app. Typically pulled when the Hola! user opens the contacts screen on her app. Sent from app.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReachabilityRequest extends APPIEvent
{
	private String usernameOrEmail; // The username or email of the user whose reachability information is being sought. E.g., "srajagopalan@pacifi.net" for Sonny. 


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public ReachabilityRequest ()
	{
	   // For Jackson ObjectMapper's sanity!
		super ("ReachabilityRequest");
	}
	public ReachabilityRequest (String _usernameOrEmail)
	{
		super ("ReachabilityRequest");
		this.usernameOrEmail = _usernameOrEmail;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("usernameOrEmail")
	public void setUsernameOrEmail (String _usernameOrEmail)
	{
		this.usernameOrEmail = _usernameOrEmail;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("usernameOrEmail")
	public String getUsernameOrEmail ()
	{
		return this.usernameOrEmail;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "type: " + getType ()+ " " + "usernameOrEmail: " + usernameOrEmail;
	}
}
