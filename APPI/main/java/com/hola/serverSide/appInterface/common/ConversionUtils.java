package com.hola.serverSide.appInterface.common;


import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ConversionUtils
{
    public static String convertIntegerIpToString (Integer ipAddressAsInt)
    {
	InetAddress inet = null;
	try
	    {
		inet = InetAddress.getByName(ipAddressAsInt.toString());
	    }
	catch (UnknownHostException e)
	    {
		e.printStackTrace ();
	    }
	return inet.getHostAddress();
    }

    public static int convertIpAddressAsStringToInteger (String ipAddressAsString)
    {
	InetAddress inet = null;
	try
	    {
		inet = InetAddress.getByName(ipAddressAsString);
	    }
	catch (UnknownHostException e)
	    {
		e.printStackTrace ();
	    }
	return ByteBuffer.wrap(inet.getAddress()).getInt();
    }

    public static ArrayList <String> getIndividualTagsFromTagSearch (String tagSearch)
    {
	ArrayList <String> allTags = new ArrayList <String> ();
	String [] tokenizedTagSearch = tagSearch.split(" ");

	for (int i = 0; i < tokenizedTagSearch.length; i++)
	    {
		if (!tokenizedTagSearch [i].equals (""))
		    {
			allTags.add (tokenizedTagSearch [i]) ;
		    }
	    }
	return allTags;
    }
}
