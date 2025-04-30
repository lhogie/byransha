package byransha.web.view;

import java.lang.reflect.Modifier;
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
	public String whatItDoes() {
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
		final Class<? extends BNode> a;
		final Class<? extends BNode> b;
		final Map<String, String> attrs;
		final boolean inheritance;

		Relation(Class<? extends BNode> a, Class<? extends BNode> b, Map<String, String> attrs, boolean inheritance) {
			this.a = a;
			this.b = b;
			this.attrs = attrs;
			this.inheritance = inheritance;
		}
	}

	public static int id(Class n) {
		return Math.abs(n.getName().hashCode());
	}

	@Override
	public EndpointTextResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			BBGraph node) throws Throwable {
		if (node == null) {
			throw new IllegalStateException(
                    "ModelDOTView executed with an incompatible node type: " +
                            "null" +
                            ". Expected BBGraph. Dispatcher logic needs correction.");
		}

		final BBGraph graphToRender = this.graph;

		return new EndpointTextResponse("text/dot", pw -> {
			var relations = new ArrayList<ModelDOTView.Relation>();
			var class_attrs = new HashMap<Class, Set<String>>();
			graphToRender.forEachNode(n -> {
				List<Class<? extends BNode>> stack = new ArrayList<>();

				for (Class<?> c = n.getClass(); c != null && BNode.class.isAssignableFrom(c) && c != BNode.class; c = c.getSuperclass()) {
					Class<? extends BNode> bnodeClass = c.asSubclass(BNode.class);

					if (ValuedNode.class.isAssignableFrom(bnodeClass) || class_attrs.containsKey(bnodeClass)) {
						continue;
					}

					class_attrs.put(bnodeClass, new HashSet<>());

					if (!stack.isEmpty()) {
						relations.add(new Relation(stack.getLast(), bnodeClass, Map.of("arrowhead", "empty"), true));
					}
					stack.add(bnodeClass);

					for (var f : bnodeClass.getDeclaredFields()) {
						if (Modifier.isStatic(f.getModifiers()) || !BNode.class.isAssignableFrom(f.getType())) {
							continue;
						}

						f.setAccessible(true);

						Class<?> fieldType = f.getType();
						Class<? extends BNode> targetType;
						boolean isList = ListNode.class.isAssignableFrom(fieldType);

						if (isList) {
							if (f.getGenericType() instanceof ParameterizedType pt && pt.getActualTypeArguments().length > 0) {
								targetType = ((Class<?>) pt.getActualTypeArguments()[0]).asSubclass(BNode.class);
							} else {
								continue;
							}
						} else {
							targetType = fieldType.asSubclass(BNode.class);
						}

						if (ValuedNode.class.isAssignableFrom(targetType)) {
							class_attrs.get(bnodeClass).add((isList ? "*" : "") + f.getName());
						} else {
							var label = f.getName();
							var attrsMap = new HashMap<>(Map.of("label", label));
							if (isList) {
								attrsMap.put("taillabel", "0..*");
							}
							relations.add(new Relation(bnodeClass, targetType, attrsMap, false));
						}
					}
				}
			});

			pw.println("digraph G {");
			pw.println("\t node [shape=record, style=filled, fillcolor=lightblue];");

			class_attrs.forEach((clazz, attrs) -> {
				pw.print("\t " + id(clazz) + " [label=\"{" + clazz.getSimpleName());
				if (!attrs.isEmpty()) {
					pw.print("|");
					attrs.stream().sorted().forEach(a -> pw.print(a + "\\l"));
				}
				pw.println("}\"];");
			});

			for (var r : relations) {
				if (class_attrs.containsKey(r.a) && class_attrs.containsKey(r.b)) {
					pw.print("\t " + id(r.a) + " -> " + id(r.b) + " [");
					r.attrs.forEach((key, value) -> pw.print(" " + key + "=\"" + value + "\""));
					pw.print(" arrowhead=" + (r.inheritance ? "empty" : "vee"));
					if (r.inheritance) {
						pw.print(" style=dashed color=gray");
					}
					pw.println(" ];");
				} else {
					System.err.println("Warning: Skipping relation involving missing class: " + r.a.getSimpleName() + " -> " + r.b.getSimpleName());
				}
			}

			pw.println("}");
		});
	}

}
