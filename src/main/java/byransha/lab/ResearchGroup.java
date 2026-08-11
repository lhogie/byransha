package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public class ResearchGroup extends Structure {

	public ResearchGroup(Element g, ID id) {
		super(g, id);
	}

	public ResearchGroup(Lab g, ID id, String name) {
		this(g, id);
		this.name.set(name);
	}

	@Override
	public String whatIsThis() {
		return "a research group";
	}

	@ShowInKishanView
	public ListNode<ACMClassifier> keywords() {
		return null;// get from publications;
	}
}
