package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Structure extends BusinessNode {
	public final StringNode name;
	public final ListNode<Structure> subStructures;
	public final ListNode<Office> offices;

	public Structure(BGraph g) {
		super(g);
		name = new StringNode(g, null, ".+");
		subStructures = new ListNode(g, "sub-structure(s)");
		offices = new ListNode(g, "offices");
	}

	@Override
	public String whatIsThis() {
		return "a physical structure having members, offices, sub-structures";
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
