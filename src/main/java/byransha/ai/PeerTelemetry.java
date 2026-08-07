package byransha.ai;

import java.io.Serializable;

public class PeerTelemetry implements Serializable {
	public double tokensPerSecond;
	public boolean isComputing;
	public double promptLag;
	public int queueSize;
	public double alpha;

	public double getScore() {
		return (tokensPerSecond * alpha) / ((1 + queueSize) * (1 + promptLag));
	}
}