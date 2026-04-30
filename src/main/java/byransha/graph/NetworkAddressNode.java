package byransha.graph;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;

import javax.swing.JLabel;

import byransha.nodes.primitive.ValuedNode;
import byransha.ui.swing.ChatSheet;

public class NetworkAddressNode extends ValuedNode<InetAddress> {

	public NetworkAddressNode(BNode parent) {
		super(parent);
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

	@Override
	public void writeKishanView(ChatSheet pane) {
		pane.add(new JLabel(get().getHostName()));
		boolean reachable = reach();
		var label = new JLabel(reachable ? "reached" : "unreachable");
		label.setForeground(reachable ? Color.GREEN : Color.RED);
		pane.add(label);
	}

	public boolean reach() {
		try {
			return get().isReachable(1);
		} catch (IOException e) {
			return false;
		}
	}
}
