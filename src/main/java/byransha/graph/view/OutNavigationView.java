package byransha.graph.view;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.swing.MyLayout;
import byransha.swing.MyLayout.Direction;

public class OutNavigationView extends NodeView<BNode> {

	public OutNavigationView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "OUTs";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {

		var p = new JPanel(new MyLayout(Direction.HORIZONTAL));
		var c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		n.forEachOut((name, out) -> {
			p.add(out.views().stream().filter(v -> v instanceof JumpTo).map(v -> (JumpTo) v).toList().getFirst()
					.createComponent(), c);
			c.gridx++;
		});

		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}