package byransha.graph.view;

import javax.swing.JCheckBox;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ByranshaUserPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class KishanView extends NodeView<BNode> {

	public KishanView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "editors for properties";
	}

	@Override
	public JsonNode toJSON() {
		return viewedNode.toJSONNode();
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		viewedNode.forEachOutInFields((f, out, readOnly) -> {
			if (out != viewedNode) {
				var cb = new JCheckBox("", out != null);
				cb.addActionListener(e -> {
					if (cb.isSelected()) {
					} else {
					}
				});
				cb.setEnabled(!readOnly);
				pane.appendToCurrentFlow(cb);
				var jt = out.createJumpComponent();
				jt.setText(f.getName());
				pane.appendToCurrentFlow(jt);
				out.getKishanView().writeTo(pane);
				pane.newLine();
			}
		});
	}

	@Override
	public void writeTo(Pane lines) {
		viewedNode.forEachOutInFields((f, out, readOnly) -> {
			if (out != viewedNode) {
				var flow = new TextFlow();
				flow.getChildren().add(new Text(readOnly ? "r-" : "rw"));
				var jt = (JumpTo) out.findView(JumpTo.class);
				jt.setLabel(f.getName());
				jt.writeTo(flow);
				out.getKishanView().writeTo(flow);
				lines.getChildren().add(flow);
			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}