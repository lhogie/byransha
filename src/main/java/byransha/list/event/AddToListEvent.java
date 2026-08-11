package byransha.list.event;

import byransha.Element;
import byransha.list.action.ListNode;
import byransha.service.system.Hub;

public class AddToListEvent<T extends Element> extends ListEvent {

	public AddToListEvent(ListNode<T> parent, Element element, int index) {
		super(parent, element, index);
	}

	@Override
	public void apply(Hub g) {
		list.elements.add(element);
	}

	@Override
	public void undo(Hub g) {
		if (list.elements.remove(index) != element) {
			throw new IllegalStateException();
		}
	}
}
