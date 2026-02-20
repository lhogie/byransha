package byransha.graph.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class AvailableActionsView extends NodeView<BNode> {

	public AvailableActionsView(BBGraph g) {
		super(g);
	}


	@Override
	public JsonNode toJSON(BNode n) {
		ArrayNode r = new ArrayNode(null);
		var actions = n.actions();
		actions.forEach(a -> r.add(a.toJSONNode()));
		return r;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		var p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		n.actions().forEach(a -> {
			var b = new JButton(a.prettyName());
			p.add(b, c);

			b.addActionListener(l -> {
				try {
					var nextNode = a.exec(n);

					if (currentUser().currentNode() != nextNode) {
						currentUser().jumpTo(nextNode);
					}
				} catch (Throwable err) {
					g.systemNode.errorLog.add(err);
					throw err instanceof RuntimeException re ? re : new RuntimeException(err);
				}
			});
		});
		return p;
	}

	

	@Override
	protected boolean allowsEditing() {
		return false;
	}

	@Override
	public String whatItShows() {
		return "the actions available on this node";
	}

}