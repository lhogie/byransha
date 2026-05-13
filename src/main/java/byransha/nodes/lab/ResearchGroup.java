package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public class ResearchGroup extends Structure {

	public ResearchGroup(BNode g) {
		super(g);
	}

	public ResearchGroup(Lab g, String name) {
		this(g);
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
