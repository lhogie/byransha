package byransha.ai;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.Hub;
import byransha.system.ChatNode;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import byransha.util.ByUtils;
import byransha.util.JsonToTreeConverter;

final public class JSONNode extends BNode {
	private final JsonNode node;

	public JSONNode(BNode g, JsonNode n) {
		super(g);
		this.node = n;
	}

	@Override
	public String whatIsThis() {
		return "JSON data";
	}

	@Override
	public ObjectNode describeAsJSON() {
		ObjectNode n = new ObjectNode(ByUtils.factory);
		n.set("json", node);
		return n;
	}

	@Override
	public String toString() {
		return "some JSON data";
	}

	@Override
	public void writeKishanView(ChatSheet sheet) {
		sheet.currentLine.add(JsonToTreeConverter.buildTreeModel(node));
	}

	@Override
	public JComponent getListItemComponent(ChatNode sheet) {
		return JsonToTreeConverter.buildTreeModel(node);
	}
}