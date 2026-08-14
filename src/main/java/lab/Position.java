package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.DateNode;

public class Position extends LabElement {
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

	public Position(InstantiationParameters p) {
		super(p);
	}

	public Position(Element parent, ID id) {
		super(parent, id);
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
