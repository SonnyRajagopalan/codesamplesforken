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
 * 11.19.2015
 * Sonny Rajagopalan/Pacifi, Inc.
 * Copyright, Pacifi, Inc.
 *
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 * The chargePerSecond of a leg used in a call.
 */
package com.hola.serverSide.ariApplication.charging;
/*
 * Import statements 
 */

public class Cost
{
    private double durationOfLeg; // The duration the leg used in call.
    private double chargePerSecond; // The chargePerSecond of caller leg.
    private String currency; // The currency of the chargePerSecond. This has to be a class later, with conversion support.

    /*
     * Constructors
     * Empty/default (see 
     *   http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
     * full, and partial (for derived classes)
     *
     */
    public Cost ()
    {
	// For Jackson ObjectMapper's sanity!
    }
    public Cost (double _durationOfLeg, double _chargePerSecond, String _currency)
    {
	this.durationOfLeg   = _durationOfLeg;
	this.chargePerSecond = _chargePerSecond;
	this.currency        = _currency;
    }
    /*
     * Setters
     */
    public void setDurationOfLeg (double _durationOfLeg)
    {
	this.durationOfLeg = _durationOfLeg;
    }


    public void setChargePerSecond (double _chargePerSecond)
    {
	this.chargePerSecond = _chargePerSecond;
    }


    public void setCurrency (String _currency)
    {
	this.currency = _currency;
    }


    /*
     * Getters
     */
    public double getDurationOfLeg ()
    {
	return this.durationOfLeg;
    }


    public double getChargePerSecond ()
    {
	return this.chargePerSecond;
    }


    public String getCurrency ()
    {
	return this.currency;
    }


}