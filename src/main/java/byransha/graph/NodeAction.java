package byransha.graph;

import java.awt.Color;

import byransha.nodes.system.User;

public abstract class NodeAction {
	public String name;

	public abstract String whatItDoes();

	public abstract BNode exec(BNode target, User user) throws Throwable;

	public Color getColor() {
		return Color.white;
	}

	public String prettyName() {
		return name;
	}
}
