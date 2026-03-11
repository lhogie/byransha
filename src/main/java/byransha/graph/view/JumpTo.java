package byransha.graph.view;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.ui.swing.ChatSheet;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

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
		return new com.fasterxml.jackson.databind.node.IntNode(viewedNode.id());
	}

	@Override
	public void writeTo(ChatSheet pane) {
		pane.appendToCurrentFlow(viewedNode.createJumpComponent());
	}

	@Override
	public void writeTo(Pane pane) {
		Button b = new Button(label != null ? label : viewedNode.prettyName());
		b.setOnAction(e -> currentUser().jumpTo(viewedNode));
		b.setPrefWidth(70);
		b.setWrapText(true);

		if (viewedNode instanceof NodeAction a) {
			var applies = a.applies();

			if (applies || g.ui.proposeUnapplicableActions.get()) {
				b.setTooltip(new Tooltip(a.whatItDoes()));
				b.setDisable(!applies);
				pane.getChildren().add(b);
			}
		} else {
			b.setTooltip(new Tooltip(viewedNode.whatIsThis()));
			pane.getChildren().add(b);
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