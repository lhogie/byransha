package byransha.ui.swing;

import javax.swing.JComponent;

import byransha.graph.Root;
import byransha.graph.BNode;
import byransha.util.PossiblyFailingConsumer;

public interface BDropTarget {

	Root g();

	public default void IdDropTarget(JComponent c, PossiblyFailingConsumer<BNode> dropAction) {
		Utils.idDropTarget(g(), c, dropAction);
	}
}
