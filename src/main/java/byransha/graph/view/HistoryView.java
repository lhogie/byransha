package byransha.graph.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class HistoryView extends NodeView<BNode> {

	public HistoryView(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItShows() {
		return "History";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ObjectNode r = new ObjectNode(BNode.factory);

		if (g.systemNode.application != null) {
			r.put(g.systemNode.application.prettyName(), g.systemNode.application.id());

			if (g.currentUser().history.backPossible()) {
				r.put("<", g.currentUser().history.backTarget().id());
			}

			if (g.currentUser().history.forwardPossible()) {
				r.put(">", g.currentUser().history.forwardTarget().id());
			}
		}
		return r;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		var p = new JPanel(new GridBagLayout());
		var c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		p.add(new JButton("<"), c);
		c.gridx++;
		p.add(new JButton(">"), c);
		c.gridx++;
		p.add(new JButton("clear"), c);
		c.gridx++;
		p.add(n.currentUser().history.findView(JumpToView.class).createComponent(n.currentUser().history), c);
		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}