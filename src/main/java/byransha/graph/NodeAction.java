package byransha.graph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.util.ByUtils;

public abstract class NodeAction<IN extends BNode, OUT extends BNode> extends BNode {

	@Hide
	protected final IN inputNode;
	public boolean stopRequested = false;
	public Thread thread;
//	public final boolean execStraightAway;
	String category;

	public NodeAction(BGraph g, IN inputNode) {
		super(g);
		this.inputNode = inputNode;
	}

	public List<BNode> parameters() {
		var r = new ArrayList<BNode>();
		forEachOutInFields(getClass(), NodeAction.class, (a, b, c) -> r.add(b));
		return r;
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new exec<OUT>(g, this));
//		super.createActions();
	}

	@Override
	public ObjectNode describeAsJSON() {
		var r = (ObjectNode) super.describeAsJSON();
		r.put("canExecute", canExecute(currentUser()));
		r.put("whatItDoes", whatItDoes());
		return r;
	}

	@Override
	public JButton createJumpButton(ChatNode chat) {
		var b = super.createJumpButton(chat);
		inputNode.changeListeners.add(n -> b.setEnabled(applies(chat)));
		var applies = applies(chat);

		if (applies || g.ui.proposeUnapplicableActions.get()) {
			b.setToolTipText(whatItDoes());
			b.setEnabled(applies);
		}

		return b;
	}

	protected final ActionResult<IN, OUT> createResultNode(OUT out, boolean jumpStraightAwayToResult) {
		return new ActionResult<IN, OUT>(g, this, out, jumpStraightAwayToResult);
	}

	public boolean canExecute(User user) {
		return true;
	}

	public boolean wantToBeProposedFor(BNode bNode) {
		return true;
	}

	@Override
	public String prettyName() {
		return ByUtils.camelToWords(getClass().getSimpleName()).replaceAll(" view", "");
	}

	public String technicalName() {
		return prettyName().replace(' ', '_').toLowerCase();
	}

	public abstract String whatItDoes();

	public abstract ActionResult<IN, OUT> exec(ChatNode chat) throws Throwable;

	@Override
	public final String whatIsThis() {
		return "an action which " + whatItDoes();
	}

	static public class exec<OUT extends BNode> extends NodeAction<NodeAction, OUT> {

		public exec(BGraph g, NodeAction inputNode) {
			super(g, inputNode);
		}

		@Override
		public String whatItDoes() {
			return "trigger the action on the source node";
		}

		boolean execStraight() {
			return getClass().getEnclosingClass() == NodeAction.class;
		}

		@Override
		public ActionResult<NodeAction, OUT> exec(ChatNode chat) throws Throwable {
//			Cout.debugSuperVisible("exec " + inputNode.prettyName());
			var startDateMs = System.currentTimeMillis();
			var r = inputNode.exec(chat);
			r.durationMs.set(System.currentTimeMillis() - startDateMs);
			return r;
		}

		@Override
		public String prettyName() {
			return "Run";
		}

		@Override
		public boolean applies(ChatNode chat) {
			return true;
		}

	}

	public abstract boolean applies(ChatNode chat);

	public String getCategory() {
		return category;
	}

}
