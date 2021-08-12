/*
 * Java code generated by beanGenerator.py
 * 02.12.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * (Derived from APPIEvent object) Authentication request message from the server to the app. Created from HTTP Authorization header in initial connect request.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import javax.websocket.Session;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest extends APPIEvent
{
	private Session        session; // The javax.websocket.Session associated with this AuthRequest.
	private EndpointConfig config; // The endpoint configuration associated with this AuthRequest.
	private String         authorizationInfo; // The authorization information from the connect request.


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public AuthRequest ()
	{
	   // For Jackson ObjectMapper's sanity!
		super ("AuthRequest");
	}
	public AuthRequest (Session _session, EndpointConfig _config, String _authorizationInfo)
	{
		super ("AuthRequest");
		this.session           = _session;
		this.config            = _config;
		this.authorizationInfo = _authorizationInfo;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("session")
	public void setSession (Session _session)
	{
		this.session = _session;
	}


	@JsonProperty ("config")
	public void setConfig (EndpointConfig _config)
	{
		this.config = _config;
	}


	@JsonProperty ("authorizationInfo")
	public void setAuthorizationInfo (String _authorizationInfo)
	{
		this.authorizationInfo = _authorizationInfo;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("session")
	public Session getSession ()
	{
		return this.session;
	}


	@JsonProperty ("config")
	public EndpointConfig getConfig ()
	{
		return this.config;
	}


	@JsonProperty ("authorizationInfo")
	public String getAuthorizationInfo ()
	{
		return this.authorizationInfo;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{
		return "type: " + getType ()+ " " + "session: " + session+ " " + "config: " + config+ " " + "authorizationInfo: " + authorizationInfo;
	}
}
