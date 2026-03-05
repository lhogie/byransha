package byransha.graph.view;

import javax.swing.JCheckBox;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.ui.swing.ByranshaUserPane;

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
		return n.toJSONNode();
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		if (n instanceof ListNode) {
			writeTo2(pane);
			return;
		}
		n.forEachOutInFields((f, out, readOnly) -> {
			if (out != n) {
				var cb = new JCheckBox("", out != null);
				cb.addActionListener(e -> {
					if (cb.isSelected()) {
					} else {
					}
				});
				cb.setEnabled(!readOnly);
				pane.append(cb);
				var jt = (JumpTo) out.findView(JumpTo.class);
				jt.setLabel(f.getName());
				jt.writeTo(pane);
				out.getKishanView().writeTo(pane);
				pane.newLine();
			}
		});
	}

	public void writeTo2(ByranshaUserPane pane) {
		((ListNode<BNode>) n).forEachOutInContent((i, out) -> {
			if (out != n) {
				var cb = new JCheckBox("", out != null);
				cb.addActionListener(e -> {
					if (cb.isSelected()) {
					} else {
					}
				});
				cb.setEnabled(!readOnly);
				pane.append(cb);
				var jt = (JumpTo) out.findView(JumpTo.class);
				jt.setLabel("" + i);
				jt.writeTo(pane);
				out.getKishanView().writeTo(pane);
				pane.newLine();
			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}