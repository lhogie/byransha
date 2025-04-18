package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.SetNode;
import byransha.StringNode;

public class Structure extends BNode {

	public StringNode name;
	public SetNode<Structure> subStructures;
	public ListNode<Person> members;
	public Person director;
	public ListNode<Status> status;
	public ListNode<Office> offices;

	public Structure(BBGraph g) {
		super(g);
		name = new StringNode(g, null);
		subStructures = new SetNode<>(g);
		members = new ListNode<>(g);
		status = new ListNode<>(g);
		offices = new ListNode<>(g);
	}

	public Structure(BBGraph g, int id) {
		super(g, id);
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