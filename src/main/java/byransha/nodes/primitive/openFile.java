package byransha.nodes.primitive;

import java.awt.Desktop;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;

public class openFile extends NodeAction<FileNode, FileNode> {

	protected openFile(BGraph g, FileNode f) {
		super(g, f);
	}

	@Override
	public String whatItDoes() {
		return "open the file";
	}

	@Override
	public ActionResult<FileNode, FileNode> exec() throws Throwable {
		Desktop desktop = Desktop.getDesktop();

		if (inputNode.file.exists()) {
			desktop.open(inputNode.file);
		}

		return createResultNode(inputNode, true);
	}

	@Override
	public String prettyName() {
		return "open";
	}

}