package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.SetNode;
import byransha.StringNode;

public class Structure extends BusinessNode {

	public StringNode name;
	public SetNode<Structure> subStructures;
	public ListNode<Person> members;
	public Person director;
	public ListNode<Status> status;
	public ListNode<Office> offices;

	public Structure(BBGraph g) {
		super(g);
		name = BNode.create(g, StringNode.class); // new StringNode(g, null);
		subStructures = BNode.create(g, SetNode.class); // new SetNode<>(g);
		members = BNode.create(g, ListNode.class); // new ListNode<>(g);
		status = BNode.create(g, ListNode.class); // new ListNode<>(g);
		offices = BNode.create(g, ListNode.class); // new ListNode<>(g);
	}

	public Structure(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a physical structure having members, offices, sub-structures";
	}

	public double occupationRatio() {
		return offices.l.stream().mapToDouble(Office::occupationRatio).average().getAsDouble();
	}

	public double avgSurfacePerUser() {
		return offices.l.stream().mapToDouble(Office::surfacePerUser).average().getAsDouble();
	}

	public double totalSurface() {
		return offices.l.stream().mapToDouble(o -> o.surface.get()).sum();
	}

	@Override
	public String prettyName() {
		return name.get();
	}
}