package byransha;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

import byransha.annotations.ListSettings;
import toools.reflect.Clazz;

import java.lang.reflect.Field;

public class ListNode<N extends BNode> extends PersistingNode {
	public final List<N> l = new CopyOnWriteArrayList<>();
	
	private static final Map<Integer, ListSettings> listSettingsCache = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

	public boolean canAddNewNode() {
		return getListSettings().allowCreation();
	}

	public boolean isDropdown() {
		return getListSettings().displayAsDropdown();
	}


	public ListNode(BBGraph db) {
		super(db);
	}

	public ListNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String whatIsThis() {
		return "a list node";
	}

	@Override
	public String prettyName() {
		return "a list";
	}

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
		int i = 0;
		for (N e : l) {
			if (e != null) {
				consumer.accept(i++ + ". " + e.prettyName(), e);
			} else {
				i++;
			}
		}
	}

	public void add(N n) {
		l.add(n);
		if (n != null && graph != null) {
			String role = "list_member_" + n.id() + "_to_" + this.id();
			

			invalidateOutsCache();
		}
		this.save(f -> {});
	}

	public void remove(N p) {
		int index = l.indexOf(p);
		if (index != -1 && p != null && graph != null) {
			String role = "list_member_" + p.id() + "_to_" + this.id();
			

			invalidateOutsCache();
		}
		l.remove(p);
		this.save(f -> {});
	}

	public void removeAll(){
		if (graph != null) {
			for (N n : l) {
				if (n != null) {
				}
			}

			invalidateOutsCache();
		}
		l.clear();
		this.save(f -> {});
	}

	public N get(int i) {
		return l.get(i);
	}

	public List<N> elements() {
		return List.copyOf(l);
	}

	public int size() {
		return l.size();
	}

	private ListSettings getListSettings() {
		if (listSettingsCache.containsKey(id())) {
			return listSettingsCache.get(id());
		}
		
		for (InLink inLink : ins()) {
			for (var c : Clazz.bfs(inLink.source().getClass())) {
				for (Field field : c.getDeclaredFields()) {
					if (field.getType().isAssignableFrom(ListNode.class)) {
						ListSettings annotation = field.getAnnotation(ListSettings.class);
						if (annotation != null) {
							listSettingsCache.put(id(), annotation);
							return annotation;
						}
					}
				}
			}
		}
		
		ListSettings defaultSettings = new ListSettings() {
			@Override
			public boolean allowCreation() {
				return true;
			}

			@Override
			public boolean displayAsDropdown() {
				return false;
			}

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return ListSettings.class;
			}
		};
		
		listSettingsCache.put(id(), defaultSettings);
		return defaultSettings;
	}
	
	public void invalidateSettingsCache() {
		listSettingsCache.remove(id());
	}

	public BNode random() {
		int currentSize = l.size();
		if (currentSize == 0) {
			return null;
		}

		return l.get(RANDOM.nextInt(currentSize));
	}

	

	public static class ListNodes extends NodeEndpoint<ListNode> implements View {
		private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

		public ListNodes(BBGraph g) {
			super(g.graph);
		}

		public ListNodes(BBGraph g, int id) {
			super(g.graph, id);
		}

		@Override
		public String whatItDoes() {
			return "returns the elements within a given list";
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, ListNode n)
				throws Throwable {

			if (n == null) {
				throw new IllegalArgumentException("The provided node is null.");
			}

			ArrayNode response = nodeFactory.arrayNode();

			for (var element : n.l) {
				ObjectNode jsonElement = nodeFactory.objectNode();
				jsonElement.put("toString", element.toString());
				response.add(jsonElement);
			}

			return new EndpointJsonResponse(response, "listNodeContents");
		}

		@Override
		public boolean sendContentByDefault() {
			return true;
		}
	}

}
