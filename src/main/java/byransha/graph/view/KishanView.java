package byransha.graph.view;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.swing.MyTextPane;

public class KishanView extends NodeView<BNode> {

	public KishanView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "editors for properties";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		return n.toJSONNode();
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		var p = new MyTextPane();

		n.forEachOut((name, out) -> {
			if (out != g) {
				var pp = new JPanel();
				p.add(new JCheckBox());
				p.add(new JLabel(name));

				for (var v : out.views()) {
					if (v.kishanable()) {
						p.add(v.createComponent());
						break;
					}
				}

				p.append(pp);
				p.append(" ");
			}
		});

		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}