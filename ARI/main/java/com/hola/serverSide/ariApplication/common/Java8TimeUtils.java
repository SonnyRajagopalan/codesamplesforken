/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.common;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Java8TimeUtils
{
    public static boolean in (LocalTime time, LocalTime start, LocalTime end)
    {
	if ((time.compareTo (start) >= 0) && (time.compareTo (end) <= 0))
	    {
		return true;
	    }
	else
	    {
		return false;
	    }
    }
}
