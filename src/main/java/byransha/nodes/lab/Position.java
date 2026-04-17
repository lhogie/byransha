package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.DateNode;

public class Position extends BNode {
	@ShowInKishanView
	public Structure employer;
	@ShowInKishanView
	public DateNode from;
	@ShowInKishanView
	public DateNode to;
	@ShowInKishanView
	public Status status;
	@ShowInKishanView
	public SupportDePoste support;

	public Position(Person g) {
		super(g);
	}

	@Override
	public String toString() {
		if (status != null && employer != null) {
			return status + " at " + employer;
		} else if (status != null) {
			return status.toString();
		} else if (employer != null) {
			return "job at " + employer;
		} else {
			return "some position";
		}
	}

	@Override
	public String whatIsThis() {
		return "a position";
	}

}
