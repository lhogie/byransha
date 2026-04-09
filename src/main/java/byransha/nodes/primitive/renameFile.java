package byransha.nodes.primitive;

import java.io.File;

import byransha.graph.BGraph;
import byransha.graph.action.ActionResult;
import byransha.graph.action.ConfirmRequiredNodeAction;
import byransha.nodes.primitive.openFile.file;
import byransha.nodes.system.ChatNode;

public class renameFile extends ConfirmRequiredNodeAction<FileNode, FileNode> {
	StringNode newName;

	public renameFile(BGraph g, FileNode inputNode) {
		super(g, inputNode, file.class);
		this.newName = new StringNode(g, inputNode.file.getName(), ".+");
	}

	@Override
	public String whatItDoes() {
		return "ranames the file";
	}

	@Override
	protected ActionResult<FileNode, FileNode> execConfirmed() {
		inputNode.file.renameTo(new File(inputNode.file.getParent(), newName.get()));
		return createResultNode(inputNode, true);
	}


	@Override
	public boolean applies(ChatNode chat) {
		return inputNode.file.exists();
	}

	
}
