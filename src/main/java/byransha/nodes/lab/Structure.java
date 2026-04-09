package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.util.ListenableList;

public class Structure extends BusinessNode {
	public final StringNode name = new StringNode(g, null, ".+");;
	public final ListNode<Structure> subStructures;
	public final ListNode<Office> offices;
	public final DynamicValuedNode<ListNode<Person>> members = new DynamicValuedNode<ListNode<Person>>(this) {

		@Override
		public ListNode<Person> exec() {
			var s = g.indexes.byClass.m.get(Person.class).stream().map(n -> (Person) n)
					.filter(p -> p.structures.elements.contains(Structure.this)).toList();
			var l = new ListNode<Person>(g, "members");
			l.elements.addAll(s);
			l.elements.addListener(new ListenableList.Listener<Person>() {

				@Override
				public void onSet(int index, Person oldElement, Person p) {
					throw new UnsupportedOperationException("not supported");
				}

				@Override
				public void onRemoved(int index, Person p) {
					p.structures.elements.remove(Structure.this);
				}

				@Override
				public void onAdded(int index, Person p) {
					p.structures.elements.add(Structure.this);
				}
			});

			return l;
		}
	};

	public final DynamicValuedNode<ListNode<Person>> members2 = new DynamicValuedNode<ListNode<Person>>(this) {

		@Override
		public ListNode<Person> exec() {
			var l = new ListNode<Person>(g, "members");
			l.elements.add(new Person(g));
			return l;
		}
	};

	@Override
	public void createActions() {
		super.createActions();
	}

	public Structure(BGraph g) {
		super(g);
		subStructures = new ListNode(g, "sub-structure(s)");
		offices = new ListNode(g, "offices");
	}

	@Override
	public String whatIsThis() {
		return "a structure";
	}

	public double occupationRatio() {
		return offices.elements.stream().mapToDouble(Office::occupationRatio).average().getAsDouble();
	}

	public double avgSurfacePerUser() {
		return offices.elements.stream().mapToDouble(Office::surfacePerUser).average().getAsDouble();
	}

	public double totalSurface() {
		return offices.elements.stream().mapToDouble(o -> o.surface.get()).sum();
	}

	@Override
	public String toString() {
		if (name == null || name.get() == null || name.get().isEmpty()) {
			return null;
		}
		return name.get();
	}

}
