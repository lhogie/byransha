package byransha.network;

import java.util.List;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.util.ByUtils;

public class MessageNode extends BNode {
	private Message m;

	public MessageNode(BNode parent, Message m) {
		super(parent);
		this.m = m;
	}

	@ShowInKishanView
	public Object content() {
		return ByUtils.serializer.fromBytes(m.content);
	}

	@ShowInKishanView
	public List<String> route() {
		return m.routingInfo.actualRoute;
	}

	@ShowInKishanView
	public String source() {
		return m.routingInfo.suggestedRoute.getFirst();
	}

	@ShowInKishanView
	public String recipient() {
		return m.routingInfo.suggestedRoute.getLast();
	}

	@ShowInKishanView
	public String routingProtocol() {
		return m.routingInfo.getClass().getName();
	}
}
