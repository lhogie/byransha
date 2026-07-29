package byransha.network;

import byransha.graph.ServiceNode;

public class ServiceLevelMessage extends Message {
	public Class<? extends ServiceNode> recipient;
	public Class<? extends ServiceNode> replyTo;

}