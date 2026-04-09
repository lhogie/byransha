package byransha.util;

import java.util.Iterator;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonToTreeConverter {
	public static JTree buildTreeModel(JsonNode rootNode) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		addNodes(root, rootNode);
		var tree = new JTree(new DefaultTreeModel(root));
		// Optional: Expand all rows by default
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		return tree;
	}

	private static void addNodes(DefaultMutableTreeNode parent, JsonNode node) {
		if (node.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(field.getKey());
				parent.add(child);
				addNodes(child, field.getValue()); // Recursion
			}
		} else if (node.isArray()) {
			for (int i = 0; i < node.size(); i++) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode("[" + i + "]");
				parent.add(child);
				addNodes(child, node.get(i)); // Recursion
			}
		} else {
			// It's a leaf node (String, Number, Boolean)
			parent.setUserObject(parent.getUserObject() + ": " + node.asText());
		}
	}

}