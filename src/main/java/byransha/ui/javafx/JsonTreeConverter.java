package byransha.ui.javafx;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.TreeItem;

public class JsonTreeConverter {

	public static TreeItem<String> buildTree(JsonNode node) {
		var parent = new TreeItem<String>();
		buildTree(node, parent);
		return parent;
	}

	public static void buildTree(JsonNode node, TreeItem<String> parent) {
		if (node.isObject()) {
			node.fields().forEachRemaining(entry -> {
				TreeItem<String> child = new TreeItem<>(entry.getKey());
				parent.getChildren().add(child);
				buildTree(entry.getValue(), child); // Recursive call
			});
		} else if (node.isArray()) {
			for (int i = 0; i < node.size(); i++) {
				TreeItem<String> child = new TreeItem<>("[" + i + "]");
				parent.getChildren().add(child);
				buildTree(node.get(i), child);
			}
		} else {
			// It's a "leaf" value (String, Number, Boolean)
			parent.setValue(parent.getValue() + " : " + node.asText());
		}
	}
}