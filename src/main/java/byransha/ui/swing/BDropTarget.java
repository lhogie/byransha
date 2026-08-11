package byransha.ui.swing;

import javax.swing.JComponent;

import byransha.graph.Hub;
import byransha.graph.Element;
import byransha.util.PossiblyFailingConsumer;

public interface BDropTarget {

	Hub g();

	public default void IdDropTarget(JComponent c, PossiblyFailingConsumer<Element> dropAction) {
		Utils.idDropTarget(g(), c, dropAction);
	}
}
