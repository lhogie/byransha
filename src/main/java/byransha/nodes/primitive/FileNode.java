package byransha.nodes.primitive;

import java.io.File;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class FileNode extends BNode {

	public File file;

	protected FileNode(BGraph g) {
		super(g);
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new openFile(g, this, "file"));
		cachedActions.elements.add(new renameFile(g, this, "file"));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a file";
	}

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

}
