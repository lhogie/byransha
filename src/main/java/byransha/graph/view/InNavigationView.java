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

public class InNavigationView extends NodeView<BNode> {

	public InNavigationView(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItShows() {
		return "INs";
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
		n.computeIns().forEach(in -> {
			p.add(in.source().views().stream().filter(v -> v instanceof JumpTo).map(v -> (JumpTo) v).toList().getFirst()
					.createComponent(in.source()), c);
			c.gridx++;
		});

		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}