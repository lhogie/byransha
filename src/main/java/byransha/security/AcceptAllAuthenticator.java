package byransha.security;

import byransha.graph.BGraph;

public class AcceptAllAuthenticator extends Authenticate {

	public AcceptAllAuthenticator(BGraph g) {
		super(g);
	}

	@Override
	public boolean test(String t, String u) {
		return true;
	}

	@Override
	public String authenticationMethods() {
		return "none";
	}
}