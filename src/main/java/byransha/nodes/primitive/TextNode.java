package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.NodeError;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.User;

public class TextNode extends PrimitiveValueNode<String> {

	StringNode labelNode;

	public TextNode(BBGraph g, String label, String data) {
		super(g);
		set(data);
		labelNode = new StringNode(g, label, ".+");
	}

	@Override
	protected byte[] valueToBytes(String s) throws IOException {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected String bytesToValue(byte[] bytes) throws IOException {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public String prettyName() {
		return "text";
	}

	@Override
	public void fromString(String s) {
		set(s);
	}

	@Override
	public String whatIsThis() {
		return "a multiline text";
	}


	@Override
	public String defaultValue() {
		return null;
	}

	
	public final class saveNodeAction extends NodeAction<TextNode, FileNode> {
		StringNode fileNameNode;

		protected saveNodeAction(BNode n, BBGraph g, User creator) {
			super(g);
			fileNameNode = new StringNode(g, "example.txt", ".+\\..+");
		}

		@Override
		public ActionResult<TextNode, FileNode> exec(TextNode target)
				throws IllegalArgumentException, IllegalAccessException, IOException {
			var path = Path.of(fileNameNode.getOrDefault(prettyName() + "-" + id() + ".txt"));
			Files.write(path, get().getBytes());
			var fileNode = new FileNode(g);
			fileNode.file = path.toFile();
			return new ActionResult(g, this, fileNode);
		}

		@Override
		public String whatItDoes() {
			return "save this text to a file";
		}

		@Override
		public String prettyName() {
			return "Save";
		}
	}
}
