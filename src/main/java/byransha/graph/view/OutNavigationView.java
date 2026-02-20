package byransha.graph.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class OutNavigationView extends NodeView<BNode> {

	public OutNavigationView(BBGraph g) {
		super(g);
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

		var p = new JPanel(new GridBagLayout());
		var c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		n.forEachOut((name, out) -> {
			p.add(out.views().stream().filter(v -> v instanceof JumpToView).map(v -> (JumpToView) v).toList().getFirst()
					.createComponent(out), c);
			c.gridx++;
		});

		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}