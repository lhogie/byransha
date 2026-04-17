package byransha.graph.relection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.Factory;
import byransha.nodes.primitive.MapNode;
import byransha.nodes.primitive.StringNode;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class ClassNode<T extends BNode> extends BNode {
	public final Class<T> representedClass;
	public ClassNode<?> superClass;

	@ShowInKishanView
	public ListNode<ClassNode> interfaces;

	@ShowInKishanView
	public MapNode<ClassNode<?>> aggregations;

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
		public String toString() {
			return name.toString();
		}
	}

	public ClassNode(BGraph g, Class c) {
		super(g);
		this.representedClass = c;
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new ShowInstances(this));
		cachedActions.elements.add(new MakeNewInstance(this));
		cachedActions.elements.add(new LinkAction(this));
		super.createActions();
	}

	public String whatItRepresents() {
		return "a " + representedClass.getSimpleName();
	}

	public void link() {
		this.interfaces = new ListNode<ClassNode>(parent, "interfaces", ClassNode.class);
		this.aggregations = new MapNode<>(this, "aggregations");

		for (var superInterface : representedClass.getInterfaces()) {
			var superInterfaceNode = g().indexes.byClass.findFirst(ClassNode.class,
					n -> n.representedClass == superInterface);

			if (superInterfaceNode != null) {
				interfaces.get().add(superInterfaceNode);
			}
		}

		this.superClass = g().indexes.byClass.findFirst(ClassNode.class,
				n -> n.representedClass == representedClass.getSuperclass());

		{
			record A(String name, Class c) {
			}

			var set = new HashSet<A>();

			for (var f : representedClass.getDeclaredFields()) {
				set.add(new A(f.getName(), f.getType()));
			}

			for (var m : representedClass.getDeclaredMethods()) {
				set.add(new A(m.getName(), m.getReturnType()));
			}

			for (var a : set) {
				var classNode = g().indexes.byClass.findFirst(ClassNode.class, n -> n.representedClass == a.c);

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
	public String toString() {
		return representedClass.getName();
	}

	public String toPlantUML(int depth, Predicate<ClassNode> filter) {
		var bfs = bfs(depth, n -> n instanceof ClassNode cn && filter.test(cn), null);
		var classes = new HashSet<>(bfs.distances.keySet().stream().map(n -> (ClassNode) n).toList());
		return toPlantUML(classes, true);
	}

	public String toPlantUML(boolean tag) {
		link();
		var buf = new StringBuilder(tag ? "@startuml\n" : "");
		buf.append("class ").append(representedClass.getSimpleName()).append("\n");

		if (superClass != null) {
			buf.append(superClass.representedClass.getSimpleName()).append(" <|-- ")
					.append(representedClass.getSimpleName()).append("\n");
		}

		for (var i : interfaces.get()) {
			buf.append(i.representedClass.getSimpleName()).append(" <|.. ").append(representedClass.getSimpleName())
					.append("\n");
		}

		for (var i : aggregations.map.entrySet()) {
			var c = i.getValue().representedClass;
			if (!c.isPrimitive() && !c.getName().startsWith("java.lang")) {
				buf.append(representedClass.getSimpleName()).append(" o-- ").append(c.getSimpleName()).append(": ")
						.append(i.getKey()).append("\n");
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

	public T newInstance(BNode parent) {
		try {
			return constructor().newInstance(parent);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException err) {
			g().errorLog.add(err);
			throw new IllegalStateException(err);
		}
	}

	public Constructor<T> constructor() {
		var candidates = new ArrayList<Constructor<T>>();

		for (var c : representedClass.getConstructors()) {
			if (c.getParameterCount() == 1 && BNode.class.isAssignableFrom(c.getParameterTypes()[0])) {
				candidates.add((Constructor<T>) c);
			}
		}

		if (candidates.size() == 1) {
			return candidates.getFirst();
		}

		for (var c : candidates) {
			if (c.isAnnotationPresent(Factory.class)) {
				return c;
			}
		}

		throw new IllegalStateException(representedClass + "");
	}

	@ShowInKishanView
	public ListNode<T> allInstances() {
		var l = new ListNode<T>(parent, "instances of " + representedClass.getSimpleName(), representedClass);
		g().indexes.byClass.m.get(representedClass).stream().map(e -> (T) e).forEach(l.elements::add);
		return l;
	}

}
