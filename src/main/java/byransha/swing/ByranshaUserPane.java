package byransha.swing;

import java.awt.Component;

import javax.swing.JComponent;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;

public interface ByranshaUserPane {

	public void append(String s);

	public void append(JComponent c);

	public void newLine();

	public Component getComponent();

	public default void addNode(BNode n) {
		clear();
		append(n.prettyName() + " - " + n.whatIsThis() + " - " + "(id:" + n.id() + ") ");
		newLine();
		n.views().getFirst().writeTo(this);

		var err = n.findView(ErrorsView.class);

		if (err.showInViewList()) {
			err.writeTo(this);
			newLine();
		}

		newLine();
		append("Available actions:");
		n.findView(AvailableActionsView.class).writeTo(this);
		newLine();
	}

	public void clear();
}
