package byransha.nodes.primitive;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;

public class BooleanRadioButtonEditor extends TradUINodeView<BooleanNode> {

	public BooleanRadioButtonEditor(BGraph g, BooleanNode node) {
		super(g, node);
	}

	@Override
	public JsonNode jsonView() {
		ObjectNode r = new ObjectNode(null);
		Boolean b = viewedNode.get();
		r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
				: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
		return r;
	}

	@Override
	public String whatItShows() {
		return "boolean editor";
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	@Override
	public JComponent getComponent() {
		var p = new JPanel(new GridLayout(1, 2));
		var yes = new JRadioButton("yes");
		var no = new JRadioButton("no");
		var group = new ButtonGroup();
		group.add(yes);
		group.add(no);
		p.add(yes);
		p.add(no);
		yes.addActionListener(e -> viewedNode.set(true));
		no.addActionListener(e -> viewedNode.set(false));
		viewedNode.valueChangeListeners.add((node, oldV, newV) -> (newV ? yes : no).setSelected(true));
		return p;
	}

}