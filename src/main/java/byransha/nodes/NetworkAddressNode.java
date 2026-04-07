package byransha.nodes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ValuedNode;

public class NetworkAddressNode extends ValuedNode<InetAddress> {

	public NetworkAddressNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new NetworkAddressView(g, this));
		super.createViews();
	}

	@Override
	public InetAddress defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(InetAddress v, ObjectOutput out) throws IOException {
		var a = v.getAddress();
		out.writeInt(a.length);
		out.write(a);

	}

	@Override
	protected InetAddress readValue(ObjectInput in) throws IOException {
		var a = new byte[in.readInt()];
		in.readFully(a);
		return InetAddress.getByAddress(a);
	}

}
