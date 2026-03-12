package byransha.nodes.primitive;

import java.awt.Desktop;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class openFile extends NodeAction<FileNode, FileNode> {

	protected openFile(BGraph g, FileNode f) {
		super(g, f);
	}

	@Override
	public String whatItDoes() {
		return "open the file";
	}

	@Override
	public ActionResult<FileNode, FileNode> exec(ChatNode chat) throws Throwable {
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

	@Override
	public boolean applies(ChatNode chat) {
		return inputNode.file.exists();
	}

}