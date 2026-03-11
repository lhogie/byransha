package byransha.nodes.primitive;

import javax.swing.JColorChooser;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

public class ColorView extends NodeView<ColorNode> {

	public ColorView(BGraph g, ColorNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
		return null;
	}

	@Override
	public String whatItShows() {
		return null;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	public void writeTo(ChatSheet pane) {
		var cc = new JColorChooser();
		cc.getSelectionModel().addChangeListener(e -> {
			viewedNode.set(cc.getColor());
		});

		pane.appendToCurrentFlow(cc);
	}

}
