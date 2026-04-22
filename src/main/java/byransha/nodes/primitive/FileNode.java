package byransha.nodes.primitive;

import java.io.File;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;

public class FileNode extends BNode {
	public File file;


	public FileNode(BNode g) {
		super(g);
	}
	
	public FileNode(BNode g, File f) {
		super(g);
		this.file = f;
	}

	@ShowInKishanView
	public StringNode name() {
		return new StringNode(parent, file.getName(), ".+");
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
