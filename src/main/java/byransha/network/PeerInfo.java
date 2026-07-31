package byransha.network;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class PeerInfo implements Serializable {
	public List<String> neighborsName;
	public PeerTelemetry aiTelemetry;
	public Properties systemProperties = System.getProperties();
	public long uptimeMs;
}
