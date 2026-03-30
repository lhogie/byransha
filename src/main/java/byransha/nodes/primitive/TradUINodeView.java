package byransha.nodes.primitive;

import javax.swing.JComponent;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.Sheet;

public abstract class TradUINodeView<N extends BNode> extends NodeView<N> {

	public TradUINodeView(BGraph g, N node) {
		super(g, node);
	}

	@Override
	public final void writeTo(Sheet pane) {
		pane.appendToCurrentLine(getComponent());
	}

	public abstract JComponent getComponent();
}