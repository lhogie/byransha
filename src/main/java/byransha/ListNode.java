package byransha;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class ListNode<N extends BNode> extends PersistingNode {
	public final List<N> l = new CopyOnWriteArrayList<>();
	public boolean canAddNewNode = true;
	public boolean isDropdown = false;


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
		this.save(f -> {});
	}

	public void remove(N p) {
		l.remove(p);
		this.save(f -> {});
	}

	public void removeById(int id) {
		l.removeIf(n -> n.id() == id);
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

	public BNode random() {
		int currentSize = l.size();
		if (currentSize == 0) {
			return null;
		}
		return l.get(new Random().nextInt(currentSize));
	}

	public void disableAddNewNode(){
		canAddNewNode = false;
	}

	public void enableIsDropdown() {
		canAddNewNode = false;
		isDropdown = true;
	}

	public static class ListNodes extends NodeEndpoint<ListNode> implements View {

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

			var response = new ArrayNode(JsonNodeFactory.instance);

			for (var element : n.l) {
				var jsonElement = new ObjectNode(JsonNodeFactory.instance);
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
