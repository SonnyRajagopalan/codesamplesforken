package com.hola.serverSide.appInterface.login;

// import java.io.IOException;
// import java.security.Principal;
import java.util.ArrayList;
// import java.util.Iterator;
import java.util.List;
// import java.util.Map;

// import javax.servlet.ServletRequest;
// import javax.security.auth.Subject;
// import javax.security.auth.callback.Callback;
// import javax.security.auth.callback.CallbackHandler;
// import javax.security.auth.callback.NameCallback;
// import javax.security.auth.callback.PasswordCallback;
// import javax.security.auth.callback.UnsupportedCallbackException;
// import javax.security.auth.login.FailedLoginException;
// import javax.security.auth.login.LoginException;
// import javax.security.auth.spi.LoginModule;

// import org.eclipse.jetty.jaas.JAASPrincipal;
// import org.eclipse.jetty.jaas.JAASRole;
// import org.eclipse.jetty.jaas.callback.ObjectCallback;
import org.eclipse.jetty.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.jaas.spi.UserInfo;
import org.eclipse.jetty.util.security.Credential;
/*
 Logging
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HolaLoginModule extends AbstractLoginModule
{
    private static final Logger log = LoggerFactory.getLogger (HolaLoginModule.class.getName ());

    // public Password extends Credential
    // {
    // 	private final String password;

    // 	public Password (String _password)
    // 	    {
    // 		this.password = _password;
    // 	    }

    // 	public 
    // }
    public UserInfo getUserInfo (String username) throws Exception
    {
	List <String> roles = new ArrayList <> ();
	roles.add ("user");
	log.debug ("Got username {}", username);
	Credential credential = (Credential) Credential.getCredential ("H014D3m06001P4$$w0rd");
	return new UserInfo (username, credential, roles);
    }							 
}
