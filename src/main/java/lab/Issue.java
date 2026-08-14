package lab;

import byransha.InstantiationParameters;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.TextNode;

public class Issue extends LabElement {
	@ShowInKishanView
	public final ListNode<LabElement> relatedTo = new ListNode<>(this,null, "related to", LabElement.class);

	@ShowInKishanView
	TextNode description = fieldNode("description", id -> new TextNode(this, id, "", ".+"));

	public Issue(InstantiationParameters p) {
		super(p);
	}
}
