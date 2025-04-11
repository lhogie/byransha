package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.SetNode;
import byransha.StringNode;

public class Structure extends BNode {

	public StringNode name = new StringNode(graph, null);
	public SetNode<Structure> subStructures = new SetNode<>(graph);
	public ListNode<Person> members = new ListNode<>(graph);
	public Person director;
	public ListNode<Status> status = new ListNode<>(graph);
	public ListNode<Office> offices = new ListNode<>(graph);

	public Structure(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a physical structure having members, offices, sub-structures";
	}

	public double occupationRatio() {
		return offices.l.stream().mapToDouble(o -> o.occupationRatio()).average().getAsDouble();
	}

	public double avgSurfacePerUser() {
		return offices.l.stream().mapToDouble(o -> o.surfacePerUser()).average().getAsDouble();
	}

	public double totalSurface() {
		return offices.l.stream().mapToDouble(o -> o.surface.get()).sum();
	}


	@Override
	public String prettyName() {
		return name.get();
	}
}