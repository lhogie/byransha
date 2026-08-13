package byransha.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.network.routing.RoutingInfo;
import byransha.service.system.Hub;
import byransha.util.ByUtils;

public class Message extends Element {
	public Object content;
	public RoutingInfo routingInfo;
	public List<Peer> actualRoute = new ArrayList<>();
	public Peer recipient;

	public ToSerialize ser = new ToSerialize();

	public static class ToSerialize implements Serializable {
		public ID messageID;
		public byte[] content;
		public ID recipientQueueAtDestination;
		public RoutingInfo routingInfo;
		public ID replyTo;
		public int errorCount;
		public int nbAttempts;
		public long emissionDateMs = System.currentTimeMillis();
		public int keepAliveMs = 10000;
		public int maxNbAttempts = 10;
		public long sendDateMs = System.currentTimeMillis();
		public List<ID> actualRoute = new ArrayList<>();
		public String recipient;
	}

	public ToSerialize toSer() {
		ser.content = ByUtils.serializer.toBytes(content);
		ser.actualRoute = actualRoute.stream().map(p -> p.id()).toList();
		ser.recipient = recipient.name;
		return ser;
	}

	public void setSer(ToSerialize s, Hub h) {
		this.ser = s;
		content = ByUtils.serializer.fromBytes(s.content);
		actualRoute = new ArrayList<>(s.actualRoute.stream().map(id -> (Peer) h.indexes.byId.get(id)).toList());
		recipient = (Peer) h.network.neighborhood.findPeerByName(s.recipient);
	}

	public void fromSer(ToSerialize ser) {
		this.actualRoute = ((List<ID>) ser.actualRoute).stream().map(id -> (Peer) hub().indexes.byId.get(id)).toList();
	}

	public Message(Element parent, ID id) {
		super(parent, id);
	}

	@ShowInKishanView
	public List<Peer> route() {
		return actualRoute;
	}

	@ShowInKishanView
	public Peer sender() {
		return actualRoute.getFirst();
	}

	@ShowInKishanView
	public Peer recipient() {
		return recipient;
	}

	@ShowInKishanView
	public Object content() {
		return content;
	}

	@ShowInKishanView
	public Peer source() {
		return actualRoute.getFirst();
	}

	@ShowInKishanView
	public String routingProtocol() {
		return routingInfo.getClass().getName();
	}

	@Override
	public String toString() {
		return "routing info: " + routingInfo + ", content:"
				+ (content != null ? content : ser.content.length + " bytes");
	}

	public boolean keepAliveExpired() {
		return age() > ser.keepAliveMs;
	}

	private long age() {
		return System.currentTimeMillis() - ser.emissionDateMs;
	}

	public long waitTimeMs() {
		return Math.max(0, Math.abs(ser.sendDateMs - System.currentTimeMillis()));
	}

	public ID recipientQueueAtDestination() {
		return ser.recipientQueueAtDestination;
	}

	public byte[] contentBytes() {
		return ser.content;
	}

}