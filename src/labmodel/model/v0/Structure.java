package labmodel.model.v0;

import byransha.BNode;
import byransha.ListNode;
import byransha.SetNode;
import byransha.StringNode;
import byransha.web.View;
import labmodel.model.v0.view.StructureView;

public class Structure extends BNode {

	static {
		View.views.add(new StructureView());
	}

	public StringNode name = new StringNode();
	public SetNode<Structure> subStructures = new SetNode<>();
	public ListNode<Person> members = new ListNode<>();
	public Person director;
	public ListNode<Status> status = new ListNode<>();
	public ListNode<Office> offices = new ListNode<>();

	public double occupationRatio() {
		return offices.l.stream().mapToDouble(o -> o.occupationRatio()).average().getAsDouble();
	}

	public double avgSurfacePerUser() {
		return offices.l.stream().mapToDouble(o -> o.surfacePerUser()).average().getAsDouble();
	}

	public double totalSurface() {
		return offices.l.stream().mapToDouble(o -> o.surface.get()).sum();
	}

}