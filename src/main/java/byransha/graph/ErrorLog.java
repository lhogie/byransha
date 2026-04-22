package byransha.graph;

import java.time.LocalDateTime;

import byransha.graph.action.ExceptionNode;
import byransha.graph.list.action.ListNode;
import byransha.nodes.system.SystemNode;

public class ErrorLog extends SystemNode {
	@ShowInKishanView
	public final ListNode<ExceptionNode> errors = new ListNode<>(this, "error(s)", ExceptionNode.class);

	public ErrorLog(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "log otf errors in the system";
	}

	@Override
	public String toString() {
		return errors.elements.size() + " error(s)";
	}

	public ExceptionNode add(Throwable err) {
		return add(err, true);
	}

	public ExceptionNode add(Throwable err, boolean rethrow) {
		var errN = new ExceptionNode(this);
		errN.err = err;
		errN.date = LocalDateTime.now();
		errors.elements.add(errN);
		err.printStackTrace();

		if (rethrow) {
			throw err instanceof RuntimeException re ? re : new RuntimeException(err);
		} else {
			err.printStackTrace();
			return errN;
		}
	}
}
