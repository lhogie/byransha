package byransha.nodes.primitive;

import java.io.File;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;

public class FileNode extends BNode {
	public File file;

	@ShowInKishanView
	public StringNode name() {
		return new StringNode(parent, file.getName(), ".+");
	}

	public FileNode(BNode g) {
		super(g);
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new openFile(this));
		cachedActions.elements.add(new renameFile(this));
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
