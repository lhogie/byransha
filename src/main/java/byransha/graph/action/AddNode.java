package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.lab.Building;
import byransha.nodes.lab.Campus;
import byransha.nodes.lab.Office;
import byransha.nodes.lab.Person;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class AddNode extends NodeAction<BNode, BNode> {
	ListNode<StringNode> classes;

	protected AddNode(BBGraph g, BNode node) {
		super(g, node);
		classes = new ListNode<StringNode>(g);
		addClass(Person.class);
		addClass(Office.class);
		addClass(Building.class);
		addClass(Campus.class);
	}

	public void addClass(Class cla) {
		classes.get().add(new StringNode(g, cla.getName(), ".*"));
	}

	@Override
	public String whatIsThis() {
		return "a node that creates other nodes";
	}

	@Override
	public String prettyName() {
		return "node creator";
	}

	@Override
	public String whatItDoes() {
		return "create a new node";
	}

	@Override
	public ActionResult<BNode, BNode> exec(BNode target) throws Throwable {
		var select = classes.getSelected();

		if (select.size() != 1)
			throw new IllegalStateException();

		return new ActionResult<BNode, BNode>(g, this,
				(BNode) Class.forName(select.getFirst().get()).getConstructor(BBGraph.class, User.class).newInstance());
	}

}
