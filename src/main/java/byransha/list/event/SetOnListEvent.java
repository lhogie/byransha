package byransha.list.event;

import byransha.Element;
import byransha.list.action.ListNode;
import byransha.service.system.Hub;

public class SetOnListEvent<T extends Element> extends ListEvent {
	T oldElement;

	public SetOnListEvent(ListNode<T> listNode, T oldElement, T newElement, int index) {
		super(listNode, newElement, index);
		this.oldElement = oldElement;
	}

	@Override
	public void apply(Hub g) {
		oldElement = (T) list.elements.set(index, element);
	}

	@Override
	public void undo(Hub g) {
		element = (T) list.elements.set(index, oldElement);
	}
}
