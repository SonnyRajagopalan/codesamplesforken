/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.common;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.List;

public class FileUtils
{
    public static List <String> getAllLinesInFileAsStringList (String filePath)
    {
	List <String> lines = null;
	try
	    {
		lines = Files.readAllLines (Paths.get (filePath), Charset.forName ("UTF-8"));		
	    }
	catch (IOException e)
	    {
		e.printStackTrace ();
	    }
	finally
	    {
		return lines;
	    }
    }

    public static String getAllLinesInFileAsString (String filePath)
    {
	String allLines = "";
	List <String> lines = null;
	try
	    {
		lines = Files.readAllLines (Paths.get (filePath), Charset.forName ("UTF-8"));
		for (String line: lines)
		    {
			allLines += line;
		    }
	    }
	catch (IOException e)
	    {
		e.printStackTrace ();
	    }
	finally
	    {
		return allLines;
	    }
    }

    public static String getAllLinesInFileAsStringWithNoSpaces (String filePath)
    {
	String fileAsString = getAllLinesInFileAsString (filePath);
	return fileAsString.replace ("\t", "").replace ("\n", "").replace (" ", "");
    }
}
