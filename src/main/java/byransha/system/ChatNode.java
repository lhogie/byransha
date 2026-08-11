package byransha.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.ai.QueryIA;
import byransha.graph.Action;
import byransha.graph.ActionMethod;
import byransha.graph.Element;
import byransha.graph.ProcedureAction;
import byransha.graph.ShowInKishanView;
import byransha.graph.action.JumpToAnotherNode;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;
import byransha.util.ByUtils;

public class ChatNode extends Element {
	@ShowInKishanView
	public ListNode<Element> nodes = new ListNode<>(this, null, "history", Element.class);
	final User user;
	private static volatile Boolean AlerteIA = false;
	public static volatile boolean NodeAIUsed = false;

	public ChatNode(User user) {
		super(user, null);
		this.user = user;
		user.chats.elements.add(this);
	}

	public Element currentNode() {
		return nodes.get().isEmpty() ? null : nodes.get().getLast();
	}

	public void append(Element n) {
		Objects.requireNonNull(n, "cannot append null node to chat");
		System.out.println("appending " + n + " to chat " + this);
		if (n instanceof QueryIA) {
			QueryIA queryIA = (QueryIA) n;
                    try {
                        if (!(InetAddress.getLocalHost().getHostName().equals(System.getenv("PUBLIC_SERVER_NAME")))) {
							if (AlerteIA == false) {
							queryIA.afficherAlerteOllama();
							
							}
						}
						AlerteIA = true;
						NodeAIUsed = true;
						if (NodeAIUsed) {
						queryIA.afficherChargementOllama();
						}
                        
						else {
							QueryIA.startOllama();
			
						}
							
                    } catch (UnknownHostException ex) {
                        System.getLogger(ChatNode.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
					
			 
		}

		if (!nodes.elements.isEmpty() && n == nodes.elements.getLast()) // if same node
			return;

		if (n instanceof Action action) {
			if (action.parameters().isEmpty()) {
				action.outputConsumer = feedback -> append(new StringNode(this, null, (String) feedback, ".*"));
				action.chat = this;
				action.execSync();

				if (action instanceof FunctionAction fa) {
					append(fa.result);
				}
			} else {
				nodes.elements.add(action);
				action.chat = this;
			}
		} else {
			nodes.elements.add(n);
		}
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Export(this));
		cachedActions.elements.add(new JumpToAnotherNode(this));
		super.createActions();
	}

	ArrayNode export() {
		ArrayNode r = new ArrayNode(ByUtils.factory);

		for (var n : nodes.elements) {
			var on = new ObjectNode(ByUtils.factory);
			r.add(on);
			on.put("id", n.id().toString());
			on.put("toString", n.toString());

			if (n instanceof ProcedureAction action) {
				var parmNode = new ObjectNode(ByUtils.factory);
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

	@ActionMethod
	public void showSuperNode() {
		append(hub());
	}
}
