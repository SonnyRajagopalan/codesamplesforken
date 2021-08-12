package com.hola.serverSide.appInterface.common;

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
