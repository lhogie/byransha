package byransha.graph.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class AvailableActionsView extends NodeView<BNode> {
	int edgeSize = 60;

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
	public Color getColor() {
		return Color.pink;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
//		var p = new JPanel(new MyLayout(Direction.HORIZONTAL));
		var actions= n.actions();
		var p = new JPanel(new GridLayout( actions.size(), 1));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		actions.forEach(a -> {
			var b = new JButton(a.prettyName());
//			b.setPreferredSize(new Dimension(edgeSize, edgeSize));
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