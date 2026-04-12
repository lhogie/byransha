package byransha.nodes.system;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.Action;
import byransha.graph.BNode;
import byransha.graph.ProcedureAction;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class ChatNode extends BNode {
	public ListNode<BNode> nodes = new ListNode<>(g, "history");
	final User user;

	public ChatNode(User user) {
		super(user.g);
		this.user = user;
		user.chatList.elements.add(this);
	}

	public BNode currentNode() {
		return nodes.get().isEmpty() ? null : nodes.get().getLast();
	}

	public void append(BNode n) {
		if (!nodes.elements.isEmpty() && n == nodes.elements.getLast()) // if same node
			return;

		if (n instanceof Action action) {
			if (action.parameters().isEmpty()) {
				action.outputConsumer = feedback -> append(new StringNode(g, (String) feedback, ".*"));
				System.out.println("executing action: " + action);
				action.chat = this;
				action.execAsync();

				if (action instanceof FunctionAction fa) {
					append(fa.result);
				}
			} else {
				System.out.println("parameters not empty, not executing: " + action.parameters());
				nodes.elements.add(action);
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
			on.put("toString", n.toString());

			if (n instanceof ProcedureAction action) {
				var parmNode = new ObjectNode(factory);
				on.set("parameters", parmNode);

				n.forEachOutInFields(n.getClass(), ProcedureAction.class,
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
