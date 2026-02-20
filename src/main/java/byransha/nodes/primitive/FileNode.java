package byransha.nodes.primitive;

import java.awt.Desktop;
import java.io.File;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.User;

public class FileNode extends BNode {

	File file;

	protected FileNode(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a file";
	}

	@Override
	public String prettyName() {
		return file.getAbsolutePath();
	}

	public static class openFile extends NodeAction<FileNode, FileNode> {

		protected openFile(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "open the file";
		}

		@Override
		public ActionResult<FileNode, FileNode> exec(FileNode target) throws Throwable {
			Desktop desktop = Desktop.getDesktop();

			if (target.file.exists()) {
				desktop.open(target.file);
			}

			return new ActionResult<FileNode, FileNode>(g, this, target);
		}

		@Override
		public String prettyName() {
			return "open";
		}

	}

}
