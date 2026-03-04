package byransha.graph;

import java.time.LocalDateTime;

import byransha.graph.action.ExceptionNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.SystemB;

public class ErrorLog extends SystemB {
	public final ListNode<ExceptionNode> errors;

	public ErrorLog(BGraph g) {
		super(g);
		errors = new ListNode<>(g, "error(s)");
	}

	@Override
	public String whatIsThis() {
		return "log of errors in the system";
	}

	@Override
	public String prettyName() {
		return "error log";
	}

	public ExceptionNode add(Throwable err) {
		var errN = new ExceptionNode(g);
		errN.err = err;
		errN.date = LocalDateTime.now();
		errors.add(errN);
		err.printStackTrace();
		return errN;
	}
}
