package byransha.security;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import byransha.graph.BGraph;

public class LdapAuthenticator extends Authenticate {

	private final String ldapUrl; // e.g. "ldap://ldap.example.com:389"
	private final String baseDn; // e.g. "dc=example,dc=com"
	private final String userAttr; // e.g. "uid" (OpenLDAP) or "sAMAccountName" (Active Directory)
	private boolean ssl;

	public LdapAuthenticator(BGraph g, String ldapUrl, String baseDn, String userAttr, boolean ssl) {
		super(g);
		int port = ssl ? 636 : 389;
		this.ldapUrl = ldapUrl + ":" + port; // "ldap://ldap.example.com:" + port,
		this.baseDn = baseDn; // "dc=example,dc=com"
		this.userAttr = userAttr; // uid
		this.ssl = ssl;
	}

	@Override
	public boolean test(String username, String password) {
		// Build the full DN: e.g. uid=john,dc=example,dc=com
		String userDn = userAttr + "=" + username + "," + baseDn;

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userDn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		if (ssl) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		try {
			new InitialDirContext(env).close();
			return true;
		} catch (NamingException e) {
			return false;
		}
	}

	@Override
	public String authenticationMethods() {
		// TODO Auto-generated method stub
		return null;
	}

}