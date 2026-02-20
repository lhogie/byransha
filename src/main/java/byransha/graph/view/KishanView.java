package byransha.graph.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class KishanView extends NodeView<BNode> {

	public KishanView(BBGraph g) {
		super(g);
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
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		n.forEachOut((name, out) -> {
			if (out != g) {
				JPanel outPanel = new JPanel(new GridBagLayout());
				outPanel.setBorder(new TitledBorder(name));
				GridBagConstraints gbfdsc = new GridBagConstraints();

				for (var v : out.views()) {
					if (v.kishanable()) {
						outPanel.add(v.createComponent(out), gbfdsc);
						gbfdsc.gridx++;
					}
				}

				p.add(outPanel, c);
				c.gridy++;
			}
		});

		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}