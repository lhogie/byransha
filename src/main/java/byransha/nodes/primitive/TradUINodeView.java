package byransha.nodes.primitive;

import javax.swing.JComponent;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

public abstract class TradUINodeView<N extends BNode> extends NodeView<N> {

	public TradUINodeView(BGraph g, N node) {
		super(g, node);
	}

	@Override
	public final void writeTo(ChatSheet pane) {
		pane.appendToCurrentLine(getComponent());
	}

	public abstract JComponent getComponent();
}