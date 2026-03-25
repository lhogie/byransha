package byransha.graph.view;

import java.awt.Dimension;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BNode;
import byransha.nodes.lab.DynamicValuedNode;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.Sheet;
import byransha.ui.swing.TextDisplayComponent;
import byransha.ui.swing.Utils;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class KishanView extends NodeView<BNode> {
	public KishanView(BNode node) {
		super(node.g, node);
	}

	@Override
	public String whatItShows() {
		return "editors for properties";
	}

	@Override
	public JsonNode jsonView() {
		return viewedNode.describeAsJSON();
	}

	@Override
	public void writeTo(Sheet sheet) {
		viewedNode.forEachOutInFields(viewedNode.getClass(), BNode.class, (f, out, readOnly) -> {
			if (out != viewedNode) {
				if (out instanceof DynamicValuedNode otf) {
					out = otf.exec();
				}

				if (out == viewedNode)
					throw new IllegalStateException();

				var fieldNameComponent = new TextDisplayComponent(g.translator, f.getName());
				fieldNameComponent.setPreferredSize(new Dimension(60, fieldNameComponent.getPreferredSize().height));
				fieldNameComponent.setToolTipText(f.getName());
				Utils.IdDropTarget(g, fieldNameComponent, dn -> viewedNode.set(f, dn));
				sheet.currentLine.add(fieldNameComponent);
//				sheet.newLine();
				sheet.currentLine.add(Utils.idShower(out, 18, 2, ((ChatSheet) sheet).chat));
				sheet.appendToCurrentLine(new ErrorIndicator(out));
				out.getViewForKishanView().writeToWithErrors(sheet);
				sheet.newLine();
			}
		});
	}

	public <N extends BNode> List<N> get(Class<N> c) {
		return g.indexes.byClass.m.get(c).stream().map(n -> (N) n).toList();
	}

	@Override
	public void writeTo(Pane lines) {
		viewedNode.forEachOutInFields(getClass(), BNode.class, (f, out, readOnly) -> {
			if (out != viewedNode) {
				var flow = new TextFlow();
				flow.getChildren().add(new Text(readOnly ? "r-" : "rw"));
				var jt = (JumpToMe) out.findView(JumpToMe.class);
				jt.setLabel(f.getName());
				jt.writeTo(flow);
				out.getViewForKishanView().writeTo(flow);
				lines.getChildren().add(flow);
			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}