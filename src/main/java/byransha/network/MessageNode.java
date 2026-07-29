package byransha.network;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;

public class MessageNode extends BNode {
	private Message m;

	public MessageNode(BNode parent, Message m) {
		super(parent);
		this.m = m;
	}

	@ShowInKishanView
	Object content() {
		return m.content;
	}

	@ShowInKishanView
	Object source() {
		return m.route;
	}

}
