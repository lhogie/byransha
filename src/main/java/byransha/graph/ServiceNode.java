package byransha.graph;

import byransha.network.Message;

public abstract class ServiceNode extends BNode {

	public ServiceNode(BNode parent) {
		super(parent);
	}

	public void onNewMessage(Message msg) {
		System.out.println(this + " received " + msg);
	};

}
