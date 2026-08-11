package byransha.action.base;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import byransha.Element;
import byransha.action.Category;
import byransha.action.ProcedureAction;
import byransha.action.Category.node;

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
