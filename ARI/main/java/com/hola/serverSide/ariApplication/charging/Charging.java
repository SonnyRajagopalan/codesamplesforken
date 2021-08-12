/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.charging;

/**
 * Charging records for a call. This object is eventually written to a database, specifically, when 
 * either party in a call hangs up.
 *
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */

import java.util.List;
import java.util.ArrayList;

public class Charging
{
    private List <ChargingRecord> chargingRecords;

    Charging ()
    {
	chargingRecords = new ArrayList <ChargingRecord> ();
    }

    public synchronized void add (ChargingRecord record)
    {
	chargingRecords.add (record);
    }

    public synchronized double getCostOfCall ()
    {
	double totalCostOfCall = 0.0;
	for (ChargingRecord record : chargingRecords)
	    {
		totalCostOfCall += record.getCallerProfile ().getCost ().getDurationOfLeg () *
		    record.getCallerProfile ().getCost ().getChargePerSecond ();
	    }
	return totalCostOfCall;
    }
}
