package com.hola.serverSide.appInterface.stormpath;

import java.util.List;
import java.util.ArrayList;

/*
  Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.resource.ResourceException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
/*
 * Hola! specific
 */

import com.hola.serverSide.appInterface.db.HolaAccountsHelper;
import com.hola.serverSide.appInterface.db.beans.HolaAccount;
import com.hola.serverSide.appInterface.db.AccountHandlesHelper;
import com.hola.serverSide.appInterface.db.beans.AccountHandles;
import com.hola.serverSide.appInterface.db.AccountPoliciesHelper;
import com.hola.serverSide.appInterface.db.beans.AccountPolicy;
import com.hola.serverSide.appInterface.appi.Handle;

public class StormpathClientSingleton
{
    // Logging
    private static final Logger log = LoggerFactory.getLogger (StormpathClientSingleton.class.getName ());

    private static final ObjectMapper objectMapper = new ObjectMapper ();

    private static Client client;
    private static Tenant tenant;
    private static Application application;
    private static final StormpathClientSingleton instance = new StormpathClientSingleton ();

    public static StormpathClientSingleton getInstance ()
    {
	return instance;
    }

    private StormpathClientSingleton ()
    {
	ClientBuilder builder = Clients.builder ();
	client = builder.build ();
	tenant = client.getCurrentTenant();
	ApplicationList applications = 
	    tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase("My Application")));
	application = applications.iterator().next();
    }

    public static boolean authenticateAUserWithStormpath (String usernameOrEmail, String rawPassword)
    {
	//Create an authentication request using the credentials
	AuthenticationRequest request = UsernamePasswordRequest.builder ()
	    .setUsernameOrEmail (usernameOrEmail)
	    .setPassword (rawPassword)
	    .build ();
	Account account = null;
	//Now let's authenticate the account with the application:
	try 
	    {
		AuthenticationResult result = application.authenticateAccount(request);
		account = result.getAccount();
	    } 
	catch (ResourceException ex) 
	    {
		log.error (ex.getStatus() + " " + ex.getMessage());
		return false;
	    }
	finally
	    {
		if (account == null)
		    {
			log.error ("Account not found in Stormpath--rejecting login!");
			return false;
		    }
		else
		    {
			if (! accountIsPresentInHola (account))
			    {
				log.debug ("Adding account for user {} into Hola! databases", 
					   account.getUsername ());
				addAnAccountToHola (account);
			    }

			// log.debug ("Name = {}", account.getFullName ());
			// log.debug ("Email = {}", account.getEmail ());
			log.debug ("Stormpath account = {}", account.toString ());
			return true;
		    }
	    }
    }

    private static boolean accountIsPresentInHola (Account account)
    {
	if (HolaAccountsHelper.getHolaAccountByUsernameOrEmail (account.getUsername ().toLowerCase ()) != null)
	    {
		return true;
	    }
	else
	    {
		return false;
	    }
    }

    private static int addAnAccountToHola (Account account)
    {
	//ObjectMapper mapper = new ObjectMapper ();
	String defaultPolicies="{\"policies\":[{\"name\":\"DefaultPolicy\",\"conditions\":[{\"type\":\"TIME_BASED_CONDITION\",\"durations\":[{\"start\":\"0000\",\"end\":\"2400\"}],\"compare\":\"IN\"}],\"actions\":{\"true\":[\"Continue\"],\"false\":[\"Continue\"]},\"availabilities\":{\"true\":[{\"device\":\"any\",\"app\":\"any\",\"method\":\"any\"}],\"false\":[{\"device\":\"any\",\"app\":\"any\",\"method\":\"any\"}]}}]}";
	List <Handle> handles = new ArrayList <Handle> ();
	Handle mainHandle = new Handle ("voip", "any", "extension");
	handles.add (mainHandle);
	// String handlesInJson = null;
	// 	try
	// 	{
	// 		handlesInJson = mapper.writeValueAsString (handles);
	// 	}
	// 	catch (JsonProcessingException e)
	// 	{
	// 		e.printStackTrace ();
	// 	}
	HolaAccount holaAccount = new HolaAccount (account.getUsername ().toLowerCase (), // Everything is lower case in our world.
						   0,
						   account.getGivenName (),
						   account.getSurname ());

	int putHolaAccountReturnCode = HolaAccountsHelper.putHolaAccount (holaAccount);

	HolaAccount retrievedAccount = HolaAccountsHelper.getHolaAccountByUsernameOrEmail (account.getUsername ().toLowerCase ());
	AccountPolicy accountPolicy = new AccountPolicy (account.getUsername ().toLowerCase (), 
							 retrievedAccount.getExtension (), defaultPolicies);
	AccountHandles accountHandles = new AccountHandles (account.getUsername ().toLowerCase (), retrievedAccount.getExtension (), "");

	int putAccountPoliciesReturnCode = AccountPoliciesHelper.putAccountPolicy (accountPolicy);
	int putAccountHandlesReturnCode  = AccountHandlesHelper.putAccountHandles (accountHandles);
	//handlesInJson);
	log.debug ("{}, {}, {}", putAccountPoliciesReturnCode, putAccountHandlesReturnCode, putHolaAccountReturnCode);
	return putHolaAccountReturnCode;
    }
}
