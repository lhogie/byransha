package byransha.nodes.lab;

import javax.swing.JComponent;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.ChatNode;

public class Structure extends BusinessNode {
	@ShowInKishanView
	public final StringNode name = new StringNode(this, null, ".+");;
	@ShowInKishanView
	public final ListNode<Structure> subStructures = new ListNode(this, "sub-structure(s)", Structure.class);
	@ShowInKishanView
	public final ListNode<Office> offices = new ListNode(this, "offices", Office.class);

	public Structure(BNode g) {
		super(g);
	}

	@ShowInKishanView
	public ListNode<Person> members() {
		return inverseRelation("members", Person.class, p -> p.structures);
	}

	@Override
	public JComponent getListItemComponent(ChatNode chat) {
		return name.getListItemComponent(chat);
	}

	@Override
	public void createActions() {
		super.createActions();
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
