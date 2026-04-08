package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public final class saveNodeAction extends NodeAction<TextNode, FileNode> {
	StringNode fileNameNode;

	protected saveNodeAction(BGraph g, TextNode textNode) {
		super(g, textNode, "file");
		fileNameNode = new StringNode(g, "example.txt", ".+\\..+");
	}

	@Override
	public ActionResult<TextNode, FileNode> exec(ChatNode chat)
			throws IllegalArgumentException, IllegalAccessException, IOException {
		var path = Path.of(fileNameNode.getOrDefault(inputNode + "-" + inputNode.id() + ".txt"));
		Files.write(path, inputNode.get().getBytes());
		var fileNode = new FileNode(g);
		fileNode.file = path.toFile();
		return createResultNode(fileNode, true);
	}

	@Override
	public String whatItDoes() {
		return "save this text to a file";
	}

	@Override
	public String toString() {
		return "Save";
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}
}