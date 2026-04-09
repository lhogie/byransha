package byransha.graph;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class CopyIDToClipboard extends NodeAction {

	public CopyIDToClipboard(BGraph g, BNode inputNode) {
		super(g, inputNode, "node");
	}

	@Override
	public String whatItDoes() {
		return "copy ID to clipboard";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		StringSelection selection = new StringSelection(inputNode.idAsText());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		return createResultNode(null, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
