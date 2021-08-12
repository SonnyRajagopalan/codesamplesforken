package com.hola.serverSide.appInterface.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class EncryptionServices
{
    public static String encryptString (String stringToBeEncrypted)
    {
	byte[] digest = null;
	
	try
	    {
		MessageDigest messageDigest = MessageDigest.getInstance ("SHA-256");
		try
		    {
			messageDigest.update (stringToBeEncrypted.getBytes("UTF-8"));
			digest = messageDigest.digest();
		    }
		catch (UnsupportedEncodingException e)
		    {
			e.printStackTrace ();
		    }
	    }
	catch (NoSuchAlgorithmException e)
	    {
		e.printStackTrace ();
	    }
	return new String (digest);
    }
}
