package byransha.nodes.primitive;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;

public class ColorView extends TradUINodeView<ColorNode> {

	public ColorView(BGraph g, ColorNode node) {
		super(g, node);
	}

	@Override
	public ObjectNode describeAsJSON() {
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

	@Override
	public JComponent getComponent() {
		var cc = new JColorChooser();
		cc.getSelectionModel().addChangeListener(e -> viewedNode.set(cc.getColor()));
		return cc;
	}

}
