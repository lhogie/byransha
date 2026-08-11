package byransha.graph;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import byransha.graph.Category.node;

public class CopyIDToClipboard extends ProcedureAction<Element> {

	public CopyIDToClipboard(Element inputNode) {
		super(inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "copy ID to clipboard";
	}

	@Override
	public void impl() throws Throwable {
		StringSelection selection = new StringSelection(inputNode.id().toBase62());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@Override
	public boolean applies() {
		return true;
	}

}
