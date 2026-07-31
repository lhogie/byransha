package byransha.network;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
	public LocalDateTime emissionDate = LocalDateTime.now();
	public RoutingInfo routingInfo;
	public byte[] content;

	@Override
	public String toString() {
		return "routing info: " + routingInfo;
	}
}