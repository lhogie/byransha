package byransha.network;

import byransha.Element;
import byransha.ID;
import byransha.action.ActionMethod;
import byransha.action.base.ShowInKishanView;
import byransha.util.Q;

public class MessageQ extends Element {
	public final Q<Message> q = new Q<>(100);

	public MessageQ(Element parent, ID id) {
		super(parent, id);
	}

	@ShowInKishanView
	public int size() {
		return q.size();
	}

	@ActionMethod
	public void clear() {
		q.clear();
	}
}
