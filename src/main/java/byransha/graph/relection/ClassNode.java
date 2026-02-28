package byransha.graph.relection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class ClassNode extends BNode {
	public final Class clazz;
	public ClassNode superClass;
	public ListNode<ClassNode> interfaces;
	public ListNode<ClassNode> aggregations;

	public static class Aggregation extends BNode {
		protected Aggregation(BBGraph g) {
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

	public ClassNode(BBGraph g, Class c) {
		super(g);
		this.clazz = c;
	}

	public void link() {
		this.interfaces = new ListNode<>(g);
		this.aggregations = new ListNode<>(g);

		for (var superInterface : clazz.getInterfaces()) {
			var superInterfaceNode = g.findFirst(ClassNode.class, n -> n.clazz == superInterface);

			if (superInterfaceNode != null) {
				interfaces.get().add(superInterfaceNode);
			}
		}

		this.superClass = g.findFirst(ClassNode.class, n -> n.clazz == clazz.getSuperclass());

		{
			var set = new HashSet<Class>();

			for (var f : clazz.getDeclaredFields()) {
				set.add(f.getType());
			}

			for (var f : clazz.getDeclaredMethods()) {
				set.add(f.getReturnType());
			}

			for (var cc : set) {
				var classNode = g.findFirst(ClassNode.class, n -> n.clazz == cc);

				if (classNode != null) {
					aggregations.get().add(classNode);
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
		var classes = bfs.distances.keySet().stream().map(n -> (ClassNode) n).toList();
		return toPlantUML(classes, true);
	}

	public String toPlantUML(boolean tag) {
		var buf = new StringBuilder(tag ? "@startuml\n" : "");
		buf.append("class ").append(clazz.getSimpleName()).append("\n");

		buf.append(superClass.clazz.getSimpleName()).append(" <|-- ").append(clazz.getSimpleName()).append(" : ")
				.append("\n");

		for (var i : interfaces.get()) {
			buf.append(i.clazz.getSimpleName()).append(" <|.. ").append(clazz.getSimpleName()).append(" : ")
					.append("\n");
		}

		for (var i : aggregations.get()) {
			if (!i.clazz.isPrimitive() && !i.clazz.getName().startsWith("java.lang")) {
				buf.append(clazz.getSimpleName()).append(" o-- ").append(i.clazz.getSimpleName()).append(" : ")
						.append("\n");
			}
		}

		buf.append(tag ? "@enduml" : "");
		return buf.toString();
	}

	public static String toPlantUML(List<ClassNode> l, boolean tag) {
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
			return (BNode) clazz.getConstructor(BBGraph.class).newInstance(g);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException err) {
			g.systemNode.errorLog.add(err);
			throw new IllegalStateException(err);
		}
	}
}
