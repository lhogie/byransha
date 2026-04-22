package byransha.graph.action;

import java.util.Arrays;

import byransha.graph.Action;
import byransha.graph.BNode;
import byransha.graph.action.FreezingAction.misc;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.TextNode;
import byransha.util.Base62;

public class JumpToAnotherNode extends Action {
	final TextNode text = new TextNode(this, "list of IDs", "");
	ListNode<BNode> nodes = new ListNode<>(this, "nodes", BNode.class);

	public JumpToAnotherNode(BNode g) {
		super(g, misc.class);
		text.valueChangeListeners.add((a, b, c) -> {
			nodes.elements.clear();
			Arrays.stream(text.get().replace(',', '\n').split("\n")).forEach(s -> {
				try {
					nodes.elements.add(g().indexes.byId.get(Base62.decode(s.trim())));
				} catch (Throwable err) {
					g().errorLog.add(err);
				}
			});
		});
	}

	@Override
	public String whatItDoes() {
		return "convert IDs to nodes";
	}

	@Override
	public void impl() {
	}

	@Override
	public boolean applies() {
		return true;
	}
}
