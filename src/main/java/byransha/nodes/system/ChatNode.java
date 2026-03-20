package byransha.nodes.system;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.list.ListNode;

public class ChatNode extends ListNode<BNode> {

	public ChatNode(User user) {
		super(user.g, user + "'s chat");
		user.chatList.elements.add(this);
		//append(initialNode);
	}

	public BNode currentNode() {
		return get().isEmpty() ? null : get().getLast();
	}

	public void append(BNode n) {
		if (size() > 0 && n == get(size() - 1)) // if same node
			return;

		if (n instanceof NodeAction action && action.parameters().isEmpty()) {
			try {
				var result = action.exec(this);

				if (result.jumpStraightAwayToOutNode) {
					append(result.outNode);
				} else {
					if (result != null)
						append(result);
				}

			} catch (Throwable err) {
				append(error(err, false));
			}
		} else {
			elements.add(n);
		}
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Export(this));
//		super.createActions();
	}

	ArrayNode export() {
		ArrayNode r = new ArrayNode(factory);

		for (var n : get()) {
			var on = new ObjectNode(factory);
			r.add(on);
			on.put("id", n.id());
			on.put("pretty name", n.prettyName());

			if (n instanceof NodeAction action) {
				var parmNode = new ObjectNode(factory);
				on.set("parameters", parmNode);

				n.forEachOutInFields(n.getClass(), NodeAction.class,
						(f, o, ro) -> parmNode.put(f.getName(), o.prettyName()));
			}
		}

		return r;
	}

}
