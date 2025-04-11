package byransha.web.view;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.DevelopmentView;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class ModelDOTView extends NodeEndpoint<BBGraph> implements DevelopmentView {

	@Override
	public String whatIsThis() {
		return "ModelDOTView provides a DOT representation of the graph.";
	}
	public ModelDOTView(BBGraph db) {
		super(db);
	}

	public ModelDOTView(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public boolean sendContentByDefault() {
		return false;
	}

	static class Relation {
		Class a, b;
		Map<String, String> attrs;
		boolean inheritance;

		Relation(Class<BNode> a, Class b, Map<String, String> attrs, boolean inheritance) {
			this.a = a;
			this.b = b;
			this.attrs = attrs;
			this.inheritance = inheritance;
		}
	}

	public static int id(Class n) {
		return Math.abs(n.hashCode());
	}

	@Override
	public EndpointTextResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			BBGraph node) throws Throwable {
		return new EndpointTextResponse("text/dot", pw -> {
			synchronized (graph.nodes) {
				System.out.println(graph.nodes.size());
			}
			var relations = new ArrayList<ModelDOTView.Relation>();
			var class_attrs = new HashMap<Class<?>, Set<String>>();
			graph.forEachNode(n -> {
				List<Class<BNode>> stack = new ArrayList<>();

				for (Class c = n.getClass(); c != null && c.getPackage() != BNode.class.getPackage()
						&& !ValuedNode.class.isAssignableFrom(c); c = c.getSuperclass()) {

					if (class_attrs.containsKey(c)) // already visited
						continue;

					class_attrs.put(c, new HashSet<>());

					if (!stack.isEmpty()) {
						relations.add(new Relation(stack.getLast(), c, Map.of("arrowhead", "empty"), true));
					}

					stack.add(c);

					for (var f : c.getDeclaredFields()) {
						if ((f.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0)
							continue;

						if (!BNode.class.isAssignableFrom(f.getType()))
							continue;

						boolean list = ListNode.class.isAssignableFrom(f.getType());
						var target = list ? (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]
								: f.getType();

						if (ValuedNode.class.isAssignableFrom(target)) {
							class_attrs.get(c).add((list ? "*" : "") + f.getName());
						} else {
							var label = f.getName();

//						var label = f.getName().equalsIgnoreCase(target.getSimpleName()) ? "" : f.getName();
							var m = new HashMap<>(Map.of("label", label));

							if (list) {
								m.put("taillabel", "0..*");
							}

							relations.add(new Relation(c, target, m, false));
						}
					}
				}
			});

			pw.println("digraph G {");

			class_attrs.entrySet().forEach(n -> {
				var clazz = n.getKey();
				var attrs = n.getValue();

				pw.print("\t " + id(clazz) + " [shape=record, label=\"{" + clazz.getSimpleName());

				if (!attrs.isEmpty()) {
					pw.print("|");
					attrs.forEach(a -> pw.print(a + "\\l"));
				}

				pw.println("}\"];");
			});

			for (var r : relations) {
				pw.print("\t " + id(r.a) + " -> " + id(r.b) + "[");
				r.attrs.entrySet().forEach(e -> pw.print(e.getKey() + "=" + '"' + e.getValue() + '"' + ','));
				pw.println("arrowhead=" + (r.inheritance ? "empty" : "vee") + "];");
			}

			pw.println("}");
		});
	}

}
