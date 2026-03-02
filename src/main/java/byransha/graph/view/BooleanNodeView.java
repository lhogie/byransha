package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.BooleanNode;

public class BooleanNodeView extends NodeView<BooleanNode> {

	public BooleanNodeView(BBGraph g, BooleanNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON(BooleanNode n) {
		ObjectNode r = new ObjectNode(null);
		Boolean b = n.get();
		r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
				: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
		return r;
	}

	@Override
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		var yes = new JRadioButton("yes");
		var no = new JRadioButton("no");
		var dunno = new JRadioButton("don't know");
		var group = new ButtonGroup();
		group.add(yes);
		group.add(no);
		group.add(dunno);

		yes.addActionListener(e -> node.set(true));
		no.addActionListener(e -> node.set(false));
		dunno.addActionListener(e -> node.set(null));

		node.valueChangeListeners.add(newValue -> {
			if (newValue == null) {
				dunno.setSelected(true);
			} else if (newValue == true) {
				yes.setSelected(true);
			} else {
				no.setSelected(true);
			}
		});

		onComponentCreated.accept(yes);
		onComponentCreated.accept(no);
		onComponentCreated.accept(dunno);
	}

	@Override
	public String whatItShows() {
		return "boolean editor";
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}