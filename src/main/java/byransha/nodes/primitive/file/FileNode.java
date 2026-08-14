package byransha.nodes.primitive.file;

import java.io.File;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.StringNode;

public class FileNode extends Element {
	public File file;

	public FileNode(Element parent, File f) {
		super(parent, null);
		this.file = f;
	}

	@ShowInKishanView
	public StringNode name() {
		return new StringNode(this, null, file.getName(), ".+");
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new openFile(this));
		cachedActions.elements.add(new renameFile(this));
		cachedActions.elements.add(new delete(this));
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
