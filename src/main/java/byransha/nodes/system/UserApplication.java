package byransha.nodes.system;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.DocumentNode;

public abstract class UserApplication extends SystemB {
	DocumentNode icon = null;
	final BNode rootNode;

	public UserApplication(BBGraph g) {
		super(g);

		try {
			this.rootNode = rootNodeClass().getConstructor(BBGraph.class).newInstance(g);
		} catch (Throwable err) {
			throw err instanceof RuntimeException re ? re : new RuntimeException(err);
		}
	}

	protected abstract Class<? extends BNode> rootNodeClass();

	public String name() {
		return rootNode == null ? null : rootNode.prettyName() == null ? null : rootNode.prettyName();
	}

	@Override
	public String whatIsThis() {
		return "a byransha-based application";
	}

	@Override
	public String prettyName() {
		return name();
	}
}
