package byransha.network;

import java.io.Serializable;

public class Message implements Serializable {
	public static class OOData {
		public Object content;
		public Peer recipient;
	}

	public final transient OOData ooInfos = new OOData();
	public long recipientNode;
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