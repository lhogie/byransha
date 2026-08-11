package byransha.ai;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.Chat;
import byransha.Element;
import byransha.ID;
import byransha.ui.swing.ChatSheet;
import byransha.util.ByUtils;
import byransha.util.JsonToTreeConverter;

final public class JSONNode extends Element {
	private final JsonNode node;

	public JSONNode(Element g, JsonNode n, ID id) {
		super(g, id);
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
	public JComponent getListItemComponent(Chat sheet) {
		return JsonToTreeConverter.buildTreeModel(node);
	}
}