package lab;

import byransha.ID;
import byransha.InstantiationParameters;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;

public class ResearchGroup extends Structure {

	public ResearchGroup(InstantiationParameters p) {
		super(p);
	}

	public ResearchGroup(Lab g, ID id, String name) {
		super(g, id);
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
