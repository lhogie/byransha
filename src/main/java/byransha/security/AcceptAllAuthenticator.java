package byransha.security;

public class AcceptAllAuthenticator extends Authenticator {

	@Override
	public boolean test(String t, String u) {
		return true;
	}

	@Override
	public String authenticationMethod() {
		return "no";
	}
}