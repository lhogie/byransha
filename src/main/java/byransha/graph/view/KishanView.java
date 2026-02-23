package byransha.graph.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
		var gl = new GridLayout();
		JPanel p = new JPanel(gl);
		n.forEachOut((name, out) -> {
			if (out != g) {
				var tf = new TextField(name);
				tf.setForeground(Color.black);
				tf.setEditable(false);
				p.add(tf);

				for (var v : out.views()) {
					if (v.kishanable()) {
						p.add(v.createComponent(out));
					}
				}
			}
		});

		int nbColumns = 2;
		gl.setColumns(nbColumns);
		gl.setRows(p.getComponentCount() / nbColumns);
		p.revalidate();
		p.repaint();
		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}