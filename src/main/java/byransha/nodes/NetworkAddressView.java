package byransha.nodes;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JLabel;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

public class NetworkAddressView extends NodeView<NetworkAddressNode> {

	public NetworkAddressView(BGraph g, NetworkAddressNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "a network address";
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		pane.add(new JLabel(viewedNode.get().getHostName()));
		boolean reachable = reach();
		var label = new JLabel(reachable ? "reached" : "unreachable");
		label.setForeground(reachable ? Color.GREEN : Color.RED);
		pane.add(label);
	}

	private boolean reach() {
		try {
			return viewedNode.get().isReachable(1);
		} catch (IOException e) {
			return false;
		}
	}

}
