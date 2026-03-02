package byransha.nodes.primitive;

import java.io.File;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class FileNode extends BNode {

	File file;

	protected FileNode(BBGraph g) {
		super(g);
	}

	@Override
	public void createActions() {
		cachedActions.add(new openFile(g, this));
		cachedActions.add(new renameFile(g, this));
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
