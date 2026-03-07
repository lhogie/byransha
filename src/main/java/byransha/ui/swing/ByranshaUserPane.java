package byransha.ui.swing;

import javax.swing.JComponent;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;

public interface ByranshaUserPane {

	public void append(String s);

	public void append(JComponent c);

	public void newLine();

	public JComponent getComponent();

	public default void addNode(BNode n) {
		clear();
		append('"' + n.prettyName() + "\" is " + n.whatIsThis() + ". Its ID is " + n.id() + ".");
		newLine();
		newLine();
		n.views().getFirst().writeTo(this);
		newLine();

		var err = n.findView(ErrorsView.class);

		if (err.showInViewList()) {
			err.writeTo(this);
			newLine();
		}
		newLine();
		newLine();
		append("What do you want to do?");
		newLine();
		n.findView(AvailableActionsView.class).writeTo(this);
		newLine();
		end();
	}

	public void end();

	public void clear();
}
