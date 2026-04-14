package byransha.graph;

import java.time.LocalDateTime;

import byransha.graph.action.ExceptionNode;
import byransha.graph.list.action.ListNode;
import byransha.nodes.system.SystemNode;

public class ErrorLog extends SystemNode {
	public final ListNode<ExceptionNode> errors = new ListNode<>(g, "error(s)", ExceptionNode.class);

	public ErrorLog(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "log of errors in the system";
	}

	@Override
	public String toString() {
		return "error log";
	}

	public ExceptionNode add(Throwable err) {
		var errN = new ExceptionNode(g);
		errN.err = err;
		errN.date = LocalDateTime.now();
		errors.elements.add(errN);
		err.printStackTrace();
		return errN;
	}
}
