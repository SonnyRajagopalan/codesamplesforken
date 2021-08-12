/*
 * Java code generated by beanGenerator.py
 * 02.23.2016
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * (Derived from APPIEvent object) Information response message from the server to the app.
 */
package com.hola.serverSide.appInterface.appi;
/*
 * Import statements 
 */
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class InfoResponse extends APPIEvent
{
	private String        extension; // The extension that has been assigned to the Hola! user.
	private String        SIPIP; // IPv4 address of the SIP server/PBX to connect to.
	private String        SIPUsername; // The username to use with the PBX.
	private String        SIPPassword; // The password to use with the PBX.
	private String        DID; // The Direct Inward Dial number to use to reach this PBX from a PSTN.
	private List<Contact> contacts; // JSON of the full list of contacts available (the address book).



	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public InfoResponse ()
	{
	   // For Jackson ObjectMapper's sanity!
		super ("InfoResponse");
	}
	public InfoResponse (String _extension, String _SIPIP, String _SIPUsername, String _SIPPassword, String _DID, List<Contact> _contacts)
	{
		super ("InfoResponse");
		this.extension   = _extension;
		this.SIPIP       = _SIPIP;
		this.SIPUsername = _SIPUsername;
		this.SIPPassword = _SIPPassword;
		this.DID         = _DID;
		this.contacts    = _contacts;
	}
	/*
	 * Setters
	 */
	@JsonProperty ("extension")
	public void setExtension (String _extension)
	{
		this.extension = _extension;
	}


	@JsonProperty ("SIPIP")
	public void setSIPIP (String _SIPIP)
	{
		this.SIPIP = _SIPIP;
	}


	@JsonProperty ("SIPUsername")
	public void setSIPUsername (String _SIPUsername)
	{
		this.SIPUsername = _SIPUsername;
	}


	@JsonProperty ("SIPPassword")
	public void setSIPPassword (String _SIPPassword)
	{
		this.SIPPassword = _SIPPassword;
	}


	@JsonProperty ("DID")
	public void setDID (String _DID)
	{
		this.DID = _DID;
	}


	@JsonProperty ("contacts")
	public void setContacts (List<Contact> _contacts)
	{
		this.contacts = _contacts;
	}


	/*
	 * Getters
	 */
	@JsonProperty ("extension")
	public String getExtension ()
	{
		return this.extension;
	}


	@JsonProperty ("SIPIP")
	public String getSIPIP ()
	{
		return this.SIPIP;
	}


	@JsonProperty ("SIPUsername")
	public String getSIPUsername ()
	{
		return this.SIPUsername;
	}


	@JsonProperty ("SIPPassword")
	public String getSIPPassword ()
	{
		return this.SIPPassword;
	}


	@JsonProperty ("DID")
	public String getDID ()
	{
		return this.DID;
	}


	@JsonProperty ("contacts")
	public List<Contact> getContacts ()
	{
		return this.contacts;
	}


	/*
	 * Override of toString() to help debug, log etc.
	 */
	@Override
	public String toString ()
	{

		String list0Str="[";
		for (Contact var: contacts) 
		{
			list0Str += var.toString () + ", ";
		}
		list0Str += "]";
		return "extension: " + extension+ " " + "SIPIP: " + SIPIP+ " " + "SIPUsername: " + SIPUsername+ " " + "SIPPassword: " + SIPPassword+ " " + "DID: " + DID+ " " + "contacts: " + list0Str;
	}
}