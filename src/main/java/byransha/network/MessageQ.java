package byransha.network;

import java.util.UUID;

import byransha.graph.ActionMethod;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.util.Q;

public class MessageQ extends BNode {
	public final Q<Message> q = new Q<>(100);

	public MessageQ(BNode parent, UUID id) {
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
