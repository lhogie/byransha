package byransha.graph;

import byransha.ID;
import byransha.graph.list.action.ListNode;
import byransha.lab.Issue;
import byransha.primitive.ValuedElement;

public abstract class LabNode extends Element {
	@ShowInKishanView
	public final ListNode<Document> documents = new ListNode<>(this, null, "document(s)", Document.class);

	public LabNode(Element parent, ID id) {
		super(parent, id);
//		generateEvents = true;
	}

	@ShowInKishanView
	public ListNode<Issue> issues() {
		return inverseRelation("issues", Issue.class, i -> i.relatedTo);
	}

	@ActionMethod
	public void reset() {
		forEachOutInFields(getClass(), Element.class, (f, o, ro) -> {
			if (!ro) {
				try {
					var v = (Element) f.get(this);

					if (v instanceof ValuedElement vn) {
						vn.reset();
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

}
