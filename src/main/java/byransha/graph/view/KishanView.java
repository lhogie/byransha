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
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		node.forEachOut((name, out) -> {
			if (out != g) {
				var p = new JPanel();
				p.setBorder(new TitledBorder(name));
				p.add(new JCheckBox());

				try {
					out.getKishanView().createSwingComponents(c -> p.add(c));
					onComponentCreated.accept(p);
				} catch (IOException e) {
					error(e);
				}

			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}