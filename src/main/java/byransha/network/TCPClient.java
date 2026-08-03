package byransha.network;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DoubleNode;
import byransha.util.ByUtils;

public class TCPClient extends ServiceNode {

	@ShowInKishanView
	final BooleanNode active = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, 5);

	public TCPClient(TCPNode net) {
		super(net);
	}

	public void start() {
		ByUtils.thread("TCP client thread", () -> {
			while (true) {
				if (active.get()) {
					for (var p : ((NetworkAgent) parent.parent).neighborhood.peers.elements) {
						if (p.getConnection() == null && p.address != null && p.autoConnect) {
							p.tryConnect();
						}
					}
				}

				sleep(periodS.get());
			}
		});
	}
}
