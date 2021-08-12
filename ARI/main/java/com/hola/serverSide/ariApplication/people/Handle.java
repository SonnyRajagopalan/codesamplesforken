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
 * 12.10.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * The handle for an app
 */
package com.hola.serverSide.ariApplication.people;
/*
 * Import statements 
 */

public class Handle
{
    private String handleApp; // The app this handle comes from.
    private String handle; // The handle value.


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
    public Handle (String _handleApp, String _handle)
    {
	this.handleApp = _handleApp;
	this.handle    = _handle;
    }
    /*
     * Setters
     */
    public void setHandleApp (String _handleApp)
    {
	this.handleApp = _handleApp;
    }


    public void setHandle (String _handle)
    {
	this.handle = _handle;
    }


    /*
     * Getters
     */
    public String getHandleApp ()
    {
	return this.handleApp;
    }


    public String getHandle ()
    {
	return this.handle;
    }


    /*
     * Overide of toString() to help debug, log etc.
     */
    @Override
    public String toString ()
    {
	return "handleApp: " + handleApp+ " " + "handle: " + handle;
    }
}
