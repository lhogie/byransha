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
	public Peer source() {
		var name = m.routingInfo.actualRoute.getFirst();
		return hub().network.neighborhood.findPeerByName(name);
	}

	@ShowInKishanView
	public Peer recipient() {
		return m.ooInfos.recipient;
	}

	@ShowInKishanView
	public String routingProtocol() {
		return m.routingInfo.getClass().getName();
	}
}
