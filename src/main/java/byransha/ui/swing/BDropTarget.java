package byransha.ui.swing;

import javax.swing.JComponent;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.util.PossiblyFailingConsumer;

public interface BDropTarget {

	BGraph g();

	public default void IdDropTarget(JComponent c, PossiblyFailingConsumer<BNode> dropAction) {
		Utils.IdDropTarget(g(), c, dropAction);
	}
}
