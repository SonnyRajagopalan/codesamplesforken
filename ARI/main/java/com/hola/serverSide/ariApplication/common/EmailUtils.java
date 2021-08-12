/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.common;


import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/*
 * Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtils
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (EmailUtils.class.getName ());

    public static void sendEmail (String recipient, String emailContents)
    {
	Properties properties = new Properties();
	properties.put("mail.smtp.host", "smtp.gmail.com");
	properties.put("mail.smtp.socketFactory.port", "465");
	properties.put("mail.smtp.socketFactory.class",
		  "javax.net.ssl.SSLSocketFactory");
	properties.put("mail.smtp.auth", "true");
	properties.put("mail.smtp.port", "465");
		
	Session session = 
	    Session.getDefaultInstance 
	    (properties,
	     new javax.mail.Authenticator() 
	     {
		 protected PasswordAuthentication getPasswordAuthentication() 
		 {
		     return new PasswordAuthentication("<InsertGmailUsernameONLYHere>", "InsertPasswordForGmailAccountHere");
		 }
	     });
	try 
	    {		
		Message message = new MimeMessage (session);
		message.setFrom (new InternetAddress("<InsertGmailUsernameONLYHere>@gmail.com"));
		message.setRecipients (Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject ("Testing from JavaMail");
		message.setText (emailContents);
		
		Transport.send (message);
		
		log.debug("Done");
		
	    } 
	catch (MessagingException e) 
	    {
		throw new RuntimeException(e);
	    }
    }
}
