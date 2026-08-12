package byransha.lab;

import byransha.BusinessElement;
import byransha.Element;
import byransha.ID;
import byransha.action.ActionMethod;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.Document;
import byransha.primitive.ValuedElement;

public abstract class LabElement extends BusinessElement {
	@ShowInKishanView
	public final ListNode<Document> documents = new ListNode<>(this, null, "document(s)", Document.class);

	public LabElement(Element parent, ID id) {
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
