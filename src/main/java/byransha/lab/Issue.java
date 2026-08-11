package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.TextNode;

public class Issue extends LabNode {
	@ShowInKishanView
	public final ListNode<LabNode> relatedTo = new ListNode<>(this,null, "related to", LabNode.class);

	@ShowInKishanView
	TextNode description = fieldNode("description", id -> new TextNode(this, id, "", ".+"));

	public Issue(Element parent, ID id) {
		super(parent, id);
	}
}
