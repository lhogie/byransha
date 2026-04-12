package byransha.security;

import java.util.function.BiPredicate;

import byransha.graph.Category;

public abstract class Authenticator implements BiPredicate<String, String> {

	public static class security extends Category {
	}

	public abstract String authenticationMethods();
}