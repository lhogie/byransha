package byransha.graph.relection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.MapNode;
import byransha.nodes.primitive.StringNode;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class ClassNode extends BNode {
	public final Class clazz;
	public ClassNode superClass;
	public ListNode<ClassNode> interfaces;
	public MapNode<ClassNode> aggregations;

	public static class Aggregation extends BNode {
		protected Aggregation(BGraph g) {
			super(g);
		}

		StringNode name;
		ClassNode target;

		@Override
		public String whatIsThis() {
			return "aggregation relation";
		}

		@Override
		public String prettyName() {
			return name.prettyName();
		}
	}

	public ClassNode(BGraph g, Class c) {
		super(g);
		this.clazz = c;
	}

	public void link() {
		this.interfaces = new ListNode<>(g, "interfaces");
		this.aggregations = new MapNode<>(g, "aggregations");

		for (var superInterface : clazz.getInterfaces()) {
			var superInterfaceNode = g.indexes.byClass.findFirst(ClassNode.class, n -> n.clazz == superInterface);

			if (superInterfaceNode != null) {
				interfaces.get().add(superInterfaceNode);
			}
		}

		this.superClass = g.indexes.byClass.findFirst(ClassNode.class, n -> n.clazz == clazz.getSuperclass());

		{
			record A(String name, Class c) {
			}

			var set = new HashSet<A>();

			for (var f : clazz.getDeclaredFields()) {
				set.add(new A(f.getName(), f.getType()));
			}

			for (var m : clazz.getDeclaredMethods()) {
				set.add(new A(m.getName(), m.getReturnType()));
			}

			for (var a : set) {
				var classNode = g.indexes.byClass.findFirst(ClassNode.class, n -> n.clazz == a.c);

				if (classNode != null) {
					aggregations.map.put(a.name, classNode);
				}
			}
		}
	}

	@Override
	public String whatIsThis() {
		return "a Java class";
	}

	@Override
	public String prettyName() {
		return clazz.getName();
	}

	public String toPlantUML(int depth, Predicate<ClassNode> filter) {
		var bfs = bfs(depth, n -> n instanceof ClassNode cn && filter.test(cn), null);
		var classes = new HashSet<>(bfs.distances.keySet().stream().map(n -> (ClassNode) n).toList());
		return toPlantUML(classes, true);
	}

	public String toPlantUML(boolean tag) {
		link();
		var buf = new StringBuilder(tag ? "@startuml\n" : "");
		buf.append("class ").append(clazz.getSimpleName()).append("\n");

		if (superClass != null) {
			buf.append(superClass.clazz.getSimpleName()).append(" <|-- ").append(clazz.getSimpleName()).append("\n");
		}

		for (var i : interfaces.get()) {
			buf.append(i.clazz.getSimpleName()).append(" <|.. ").append(clazz.getSimpleName()).append("\n");
		}

		for (var i : aggregations.map.entrySet()) {
			var c = i.getValue().clazz;
			if (!c.isPrimitive() && !c.getName().startsWith("java.lang")) {
				buf.append(clazz.getSimpleName()).append(" o-- ").append(c.getSimpleName()).append(": ").append(i.getKey())
						.append("\n");
			}
		}

		buf.append(tag ? "@enduml" : "");
		return buf.toString();
	}

	public static String toPlantUML(Set<ClassNode> l, boolean tag) {
		var buf = new StringBuilder(tag ? "@startuml\n" : "");
		l.forEach(e -> buf.append(e.toPlantUML(false)));
		buf.append(tag ? "@enduml" : "");
		return buf.toString();
	}

	public String toSVG() {
		var out = new ByteArrayOutputStream();

		try {
			SourceStringReader reader = new SourceStringReader(toPlantUML(true));
			reader.outputImage(out, new FileFormatOption(FileFormat.SVG));
			out.close();
		} catch (IOException err) {
			throw new IllegalStateException(err);
		}

		return new String(out.toByteArray());
	}

	public BNode newInstance() {
		try {
			return (BNode) clazz.getConstructor(BGraph.class).newInstance(g);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException err) {
			g.errorLog.add(err);
			throw new IllegalStateException(err);
		}
	}

	public static ClassNode find(BGraph g, Class cla) {
		return g.indexes.byClass.findFirstOr(ClassNode.class, n -> n.clazz == cla, () -> new ClassNode(g, cla));
	}

}
