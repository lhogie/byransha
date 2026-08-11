package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.DateNode;

public class Position extends LabNode {
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

	public Position(Element g, ID id) {
		super(g, id);
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
