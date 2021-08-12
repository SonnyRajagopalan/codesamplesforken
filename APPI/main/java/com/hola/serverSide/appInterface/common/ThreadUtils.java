package com.hola.serverSide.appInterface.common;

public class ThreadUtils
{
    public static String getThreadIDString ()
    {
	return "[Thread_ID" + Thread.currentThread ().getId () + "]";
    }

    public static String getStackTraceString ()
    {
	StackTraceElement[] st    = Thread.currentThread ().getStackTrace ();
	int      stLen = st.length;
	String fullST = "";

	for (int j = 2; j < stLen; j++)
	    {
		fullST += st [j].toString () + "\n";
	    }
	return fullST;
    }

    public static String getThreadIDedStackTraceString ()
    {
	StackTraceElement[] st    = Thread.currentThread ().getStackTrace ();
	int      stLen = st.length;
	String fullST = "";

	for (int j = 2; j < stLen; j++)
	    {
		fullST += "[Thread_ID: " + Thread.currentThread ().getId () + "] " + st [j].toString () + "\n";
	    }
	return fullST;
    }
}
