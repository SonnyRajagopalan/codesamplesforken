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
 * (Derived from Event object) Event showing the start of a media playback operation.
 */
package com.hola.serverSide.ariApplication.ari;
/*
 * Import statements 
 */
import java.sql.Timestamp;

public class PlaybackStarted extends Event
{
	private Playback  playback; // Playback control object


	/*
	 * Constructors
	 * Empty/default (see 
	 *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	 * full, and partial (for derived classes)
	 *
	 */
	public PlaybackStarted ()
	{
	   // For Jackson ObjectMapper's sanity!
	}
	public PlaybackStarted (String _type, String _application, Timestamp _timestamp, Playback _playback)
	{
		super (_type, _application, _timestamp);
		this.playback    = _playback;
	}
	/*
	 * Setters
	 */
	public void setPlayback (Playback _playback)
	{
		this.playback = _playback;
	}


	/*
	 * Getters
	 */
	public Playback getPlayback ()
	{
		return this.playback;
	}


}
