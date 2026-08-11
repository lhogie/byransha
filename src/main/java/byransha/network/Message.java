package byransha.network;

import java.io.Serializable;
import java.util.List;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.network.routing.RoutingInfo;

public class Message extends Element implements Serializable {

	public static class OOData {
		public Object content;
		public Peer recipient;
	}

	public transient OOData ooInfos = new OOData();
	public ID recipientQueueAtDestination;
	public long replyTo;
	public RoutingInfo routingInfo = new RoutingInfo();
	public int errorCount;
	public int nbAttempts;
	public long emissionDateMs = System.currentTimeMillis();
	public int keepAliveMs = 10000;
	public int maxNbAttempts = 10;
	public long sendDateMs = System.currentTimeMillis();

	public byte[] content;
//	public transient Object contentObject;

	public Message(Element parent, ID id) {
		super(parent, id);
	}

	@ShowInKishanView
	public Object content() {
		return ooInfos.content;
	}

	@ShowInKishanView
	public List<String> route() {
		return routingInfo.actualRoute;
	}

	@ShowInKishanView
	public Peer source() {
		var name = routingInfo.actualRoute.getFirst();
		return hub().network.neighborhood.findPeerByName(name);
	}

	@ShowInKishanView
	public Peer recipient() {
		return ooInfos.recipient;
	}

	@ShowInKishanView
	public String routingProtocol() {
		return routingInfo.getClass().getName();
	}

	@Override
	public String toString() {
		return "routing info: " + routingInfo + ", content:" + content.length;
	}

	public boolean keepAliveExpired() {
		return age() > keepAliveMs;
	}

	private long age() {
		return System.currentTimeMillis() - emissionDateMs;
	}

	public long waitTimeMs() {
		return Math.max(0, Math.abs(sendDateMs - System.currentTimeMillis()));
	}
}