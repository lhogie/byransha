package byransha.graph.view;

import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

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
	public void addTo(Consumer<JComponent> onComponentCreated) {
		node.forEachOut((name, out) -> {
			if (out != g) {
				var p = new JPanel();
				p.setBorder(new TitledBorder(name));
				p.add(new JCheckBox());

				for (var v : out.views()) {
					if (v.kishanable()) {
						try {
							v.addTo(c -> p.add(c));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}

				onComponentCreated.accept(p);
			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}