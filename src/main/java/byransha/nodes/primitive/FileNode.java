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
		cachedActions.add(new openFile(g, this));
		cachedActions.add(new renameFile(g, this));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a file";
	}

	@Override
	public String prettyName() {
		return file.getAbsolutePath();
	}

}
