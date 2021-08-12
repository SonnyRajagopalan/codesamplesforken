package com.hola.serverSide.appInterface.init;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@WebServlet
public class APPIInit //extends javax.servlet.http.HttpServlet
{
    private static final Logger log = LoggerFactory.getLogger (APPIInit.class.getName ());

    public static void main (String [] args)
    {
	log.debug ("Initializing the app interface....");
    }
}
