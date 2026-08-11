package byransha.graph.list.event;

import byransha.graph.Element;
import byransha.graph.Hub;
import byransha.graph.list.action.ListNode;

public class RemoveFromListEvent<T extends Element> extends ListEvent {

	public RemoveFromListEvent(ListNode parent, Element element, int index) {
		super(parent, element, index);
	}

	@Override
	public void apply(Hub g) {
		list.elements.remove(index);
	}

	@Override
	public void undo(Hub g) {
		list.elements.add(index, element);
	}
}
