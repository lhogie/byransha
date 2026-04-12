package byransha.graph;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import byransha.graph.BNode.node;

public class CopyIDToClipboard extends ProcedureAction<BNode> {

	public CopyIDToClipboard(BNode inputNode) {
		super(inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "copy ID to clipboard";
	}

	@Override
	public void impl() throws Throwable {
		StringSelection selection = new StringSelection(inputNode.idAsText());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@Override
	public boolean applies() {
		return true;
	}

}
