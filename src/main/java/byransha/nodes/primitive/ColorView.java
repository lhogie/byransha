package byransha.nodes.primitive;

import javax.swing.JColorChooser;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ByranshaUserPane;

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

	public void writeTo(ByranshaUserPane pane) {
		var cc = new JColorChooser();
		cc.getSelectionModel().addChangeListener(e -> {
			n.set(cc.getColor());
		});

		pane.append(cc);
	}

}
