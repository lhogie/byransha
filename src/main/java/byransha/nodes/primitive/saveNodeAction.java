package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;

public final class saveNodeAction extends NodeAction<TextNode, FileNode> {
	StringNode fileNameNode;

	protected saveNodeAction(BGraph g, TextNode textNode) {
		super(g, textNode);
		fileNameNode = new StringNode(g, "example.txt", ".+\\..+");
	}

	@Override
	public ActionResult<TextNode, FileNode> exec()
			throws IllegalArgumentException, IllegalAccessException, IOException {
		var path = Path.of(fileNameNode.getOrDefault(prettyName() + "-" + id() + ".txt"));
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
	public String prettyName() {
		return "Save";
	}
	

	@Override
	public boolean applies() {
		return true;
	}
}