package byransha.graph;

import java.time.LocalDateTime;

import byransha.graph.action.ExceptionNode;
import byransha.graph.list.action.ListNode;

public class ErrorLog extends Element {
	@ShowInKishanView
	public final ListNode<ExceptionNode> errors = new ListNode<>(this, id().augmentWith("errors"), "error(s)",
			ExceptionNode.class);

	public ErrorLog(Hub g) {
		super(g, null);
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
		var errN = new ExceptionNode(this, null);
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
