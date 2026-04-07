package byransha.nodes.system;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.list.ListNode;

public class ChatNode extends BNode {
	public ListNode<BNode> nodes = new ListNode<BNode>(g, "history");
	final User user;

	public ChatNode(User user) {
		super(user.g);
		this.user = user;
		user.chatList.elements.add(this);
		// append(initialNode);
	}

	public BNode currentNode() {
		return nodes.get().isEmpty() ? null : nodes.get().getLast();
	}

	public void append(BNode n) {
		if (!nodes.elements.isEmpty() && n == nodes.elements.getLast()) // if same node
			return;

		if (n instanceof NodeAction action && action.parameters().isEmpty()) {
			try {
				var actionController = action.exec(this);

				if (actionController.jumpStraightAwayToResult && actionController.result != null) {
					append(actionController.result);
				} else if (actionController != null) {
					append(actionController);
				}

			} catch (Throwable err) {
				append(error(err, false));
			}
		} else {
			nodes.elements.add(n);
		}
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Export(this));
//		super.createActions();
	}

	ArrayNode export() {
		ArrayNode r = new ArrayNode(factory);

		for (var n : nodes.elements) {
			var on = new ObjectNode(factory);
			r.add(on);
			on.put("id", n.id());
			on.put("pretty name", n.toString());

			if (n instanceof NodeAction action) {
				var parmNode = new ObjectNode(factory);
				on.set("parameters", parmNode);

				n.forEachOutInFields(n.getClass(), NodeAction.class,
						(f, o, ro) -> parmNode.put(f.getName(), o.toString()));
			}
		}

		return r;
	}

	@Override
	public String whatIsThis() {
		return "a chat";
	}

	@Override
	public String toString() {
		return user + "'s chat";
	}

}
