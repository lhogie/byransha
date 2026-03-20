package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.ChatNode;

public class Structure extends BusinessNode {
	public final StringNode name;
	public final ListNode<Structure> subStructures;
	public final ListNode<Office> offices;
	public final OnTheFlyNode<ListNode<Person>> members = new OnTheFlyNode<>(this) {

		@Override
		public ListNode<Person> compute() {
			var s = g.indexes.nodesList.stream().filter(n -> n instanceof Person).map(n -> (Person) n)
					.filter(p -> p.researchGroup == Structure.this).toList();
			var l = new ListNode<Person>(g, "members");
			l.elements.addAll(s);
			return l;
		}
	};

	@Override
	public void createActions() {
		super.createActions();
	}

	public Structure(BGraph g) {
		super(g);
		name = new StringNode(g, null, ".+");
		subStructures = new ListNode(g, "sub-structure(s)");
		offices = new ListNode(g, "offices");
	}

	@Override
	public String whatIsThis() {
		return "a structure";
	}

	public double occupationRatio() {
		return offices.elements().stream().mapToDouble(Office::occupationRatio).average().getAsDouble();
	}

	public double avgSurfacePerUser() {
		return offices.elements().stream().mapToDouble(Office::surfacePerUser).average().getAsDouble();
	}

	public double totalSurface() {
		return offices.elements().stream().mapToDouble(o -> o.surface.get()).sum();
	}

	@Override
	public String prettyName() {
		if (name == null || name.get() == null || name.get().isEmpty()) {
			return null;
		}
		return name.get();
	}

	
}
