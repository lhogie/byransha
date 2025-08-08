package byransha.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.Changer;
import byransha.User;
import toools.text.TextUtilities;

public abstract class Endpoint extends BNode {
	public static <E extends Endpoint> E create(Class<E> e, BBGraph g) {
		try {
			return e.getConstructor(BBGraph.class).newInstance(g);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			throw new IllegalStateException(e1);
		}
	}

	public final AtomicInteger nbCalls = new AtomicInteger(0);
	public final AtomicLong timeSpentNs = new AtomicLong(0);

	protected Endpoint(BBGraph db) {
		super(db);
	}


	@Override
	public final String whatIsThis() {
		return whatItDoes();
	}

	public abstract String whatItDoes();

	public abstract EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange)
			throws Throwable;

	public boolean requiresAuthentication() {
		return true;
	}

	public boolean canExec(User user) {
		return true;
	}

	@Override
	public boolean canSee(User user) {
		boolean isGuestUser = user != null && user.name.get().equals("guest");
		return !this.requiresAuthentication() || true;// TODO: add back || !isGuestUser;
	}

	public <N extends BNode> Class<N> getTargetNodeType() {
		for (Class<? extends Endpoint> c = getClass(); c != null; c = (Class<? extends Endpoint>) c.getSuperclass()) {
			var t = c.getGenericSuperclass();

			if (t instanceof ParameterizedType pt) {
				return (Class<N>) pt.getActualTypeArguments()[0];
			}
		}

		throw new IllegalStateException();
	}

	public final String name() {
		var name = getClass().getSimpleName();

		var enclosingClass = getClass().getEnclosingClass();

		if (enclosingClass != null) {
			name = enclosingClass.getSimpleName() + "_" + name;
		}

		return TextUtilities.camelToSnake(name);
	}
	
	@Override
	public String prettyName() {
		return TextUtilities.camelToSnake(getClass().getSimpleName()).replace('_', ' ');
	}


	protected final JsonNode requireParm(ObjectNode in, String s) {
		var node = in.remove(s);

		if (node == null) {
			throw new IllegalArgumentException("missing parameter: " + s);
		} else {
			return node;
		}
	}
	


	public boolean isDevelopmentView() {
		return DevelopmentView.class.isAssignableFrom(getClass());
	}

	public boolean isTechnicalView() {
		return TechnicalView.class.isAssignableFrom(getClass());
	}
	
	public boolean isChanger() {
		return Changer.class.isAssignableFrom(getClass());
	}

	public static class V extends NodeEndpoint<Endpoint> {
		public V(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "describes endpoint";
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				Endpoint endpoint) throws Throwable {
			return new EndpointTextResponse("text/html", pw -> {
				pw.println("<ul>");
				pw.println("<li>name: " + endpoint.name());
				pw.println("<li>label: " + endpoint.prettyName());
				pw.println("<li>target: " + endpoint.getTargetNodeType().getName());

				pw.println("<li>calls: " + endpoint.nbCalls.get());
				pw.println("<li>time (ms): " + endpoint.timeSpentNs.get() / 1_000_000.0);

				if (endpoint instanceof View v) {
					pw.println("<li>development" + isDevelopmentView());
					pw.println("<li>technical" + endpoint.isTechnicalView());
					pw.println("<li>content by default" + v.sendContentByDefault());
				}
				pw.println("</ul>");
			});
		}

		
	}
}
