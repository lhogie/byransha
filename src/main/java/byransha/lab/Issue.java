package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.primitive.TextNode;

public class Issue extends LabNode {
	@ShowInKishanView
	public final ListNode<LabNode> relatedTo = new ListNode<>(this,null, "related to", LabNode.class);

	@ShowInKishanView
	TextNode description = lookupOrCreate("description", id -> new TextNode(this, id, "", ".+"));

	public Issue(Element parent, ID id) {
		super(parent, id);
	}
}
