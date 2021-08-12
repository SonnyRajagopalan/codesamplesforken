/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.common;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.UUID;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Random;

public class SessionTokenUtils
{
    public static String getARandomAsciiString (int length)
    {
	return RandomStringUtils.randomAscii (length);
    }
    
    public static String getARandomAlphabeticString (int length)
    {
	return RandomStringUtils.randomAlphabetic (length).toUpperCase ();
    }

    public static String getARandomNumberString (int length)
    {
	return RandomStringUtils.random (length, false, true);
    }
    
    public static String getARandomUUID ()
    {
	return UUID.randomUUID ().toString ().replaceAll("-", "");
    }

    public static String getASecureRandomNumber (int numberOfBytes)
    {
	// See http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string?page=1&tab=votes#tab-top
	String secureString = "";
	try
	    {
		SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
		secureString =  new BigInteger (130, secureRandomGenerator).toString (numberOfBytes);
	    }
	catch (NoSuchAlgorithmException e)
	    {
		e.printStackTrace ();
	    }
	return secureString;
    }
}
