package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.User.JumpListener;

public class ChatNode extends ListNode<BNode> {
	public final List<JumpListener> jumpListeners = new ArrayList<>();

	public ChatNode(BGraph g) {
		super(g, "navigation history");
	}

	public void addToHistory(BNode n) {
		get().add(n);
	}

	public BNode currentNode() {
		return get().isEmpty() ? null : get().getLast();
	}
	public void jumpTo(BNode n) {
		if (n instanceof NodeAction a && a.execStraightAway) {
			try {
				ActionResult ar = a.exec();
				jumpTo(ar);

				if (ar.jumpStraightAwayToResult) {
					jumpTo(ar.result);
				}
			} catch (Throwable err) {
				jumpTo(error(err, false));
			}
		} else {
			addToHistory(n);
			jumpListeners.forEach(l -> l.userJumpedTo(n));
		}
	}
}
