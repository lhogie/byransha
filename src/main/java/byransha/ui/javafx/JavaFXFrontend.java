package byransha.ui.javafx;

import byransha.graph.BGraph;
import byransha.nodes.system.SystemNode;

public class JavaFXFrontend extends SystemNode {

	public JavaFXFrontend(BGraph g) {
		super(g);
		currentUser().jumpTo(currentUser());
	}

	@Override
	public String whatIsThis() {
		return "the JavaFX GUI";
	}

	@Override
	public String prettyName() {
		return "JavaFX GUI";
	}

}
