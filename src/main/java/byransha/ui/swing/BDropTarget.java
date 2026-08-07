package byransha.ui.swing;

import javax.swing.JComponent;

import byransha.graph.Hub;
import byransha.graph.BNode;
import byransha.util.PossiblyFailingConsumer;

public interface BDropTarget {

	Hub g();

	public default void IdDropTarget(JComponent c, PossiblyFailingConsumer<BNode> dropAction) {
		Utils.idDropTarget(g(), c, dropAction);
	}
}
