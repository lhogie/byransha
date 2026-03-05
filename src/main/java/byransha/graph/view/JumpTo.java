package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.ui.swing.ByranshaUserPane;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextFlow;

public class JumpTo extends NodeView<BNode> {

	private String label;
	private JButton b;

	public JumpTo(BGraph g, BNode node) {
		super(g, node);
	}

	protected boolean kishanable() {
		return true;
	}

	@Override
	public String whatItShows() {
		return "a way to jump to the node";
	}

	public void setLabel(String l) {
		this.label = l;

		if (b != null) {
			b.setText(label);
		}
	}

	@Override
	public JsonNode toJSON() {
		return new com.fasterxml.jackson.databind.node.IntNode(n.id());
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		b = new JButton(label != null ? label : n.prettyName());
		int side = 70;
		b.setPreferredSize(new Dimension(side, side));
		b.setMaximumSize(b.getPreferredSize());
		b.addActionListener(e -> currentUser().jumpTo(n));
		pane.append(b);

		if (n instanceof NodeAction a) {
			b.setToolTipText(a.whatItDoes());
			b.setEnabled(a.applies());
		} else {
			b.setToolTipText(n.whatIsThis());
		}
	}

	@Override
	public void writeTo(TextFlow pane) {
		Button b = new Button(label != null ? label : n.prettyName());
		b.setOnAction(e -> currentUser().jumpTo(n));
		pane.getChildren().add(b);

		if (n instanceof NodeAction a) {
			b.setTooltip(new Tooltip(a.whatItDoes()));
			b.setDisable(!a.applies());
		} else {
			b.setTooltip(new Tooltip(n.whatIsThis()));
		}
	}

	public boolean showInViewList() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}