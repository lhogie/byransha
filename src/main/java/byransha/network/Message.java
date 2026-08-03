package byransha.network;

import java.io.Serializable;

public class Message implements Serializable {
	public long recipient;
	public long replyTo;
	public RoutingInfo routingInfo = new RoutingInfo();
	public int errorCount;
	public int nbAttempts;
	public long emissionDateMs = System.currentTimeMillis();
	public int keepAliveMs = 10000;
	public int maxNbAttempts = Integer.MAX_VALUE;

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
}