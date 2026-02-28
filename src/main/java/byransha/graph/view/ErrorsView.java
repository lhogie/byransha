package byransha.graph.view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.swing.MyLayout;
import byransha.swing.MyLayout.Direction;

public class ErrorsView extends NodeView<BNode> {

	public ErrorsView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "errors";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ArrayNode r = new ArrayNode(factory);

		for (var err : n.errors()) {
			r.add(err.msg);
		}

		return r;
	}

	@Override
	protected boolean kishanable() {
		return true;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		var p = new JPanel(new MyLayout(Direction.HORIZONTAL));

		for (var err : n.errors()) {
			var b = new JLabel(err.msg);
			p.add(b);
		}

		return p;
	}

	public boolean showInViewList() {
		return true;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}