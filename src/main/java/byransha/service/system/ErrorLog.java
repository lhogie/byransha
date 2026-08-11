package byransha.service.system;

import java.time.LocalDateTime;

import byransha.Element;
import byransha.ExceptionElement;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;

public class ErrorLog extends Element {
	@ShowInKishanView
	public final ListNode<ExceptionElement> errors = new ListNode<>(this, null, "error(s)", ExceptionElement.class);

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

	public ExceptionElement add(Throwable err) {
		return add(err, true);
	}

	public ExceptionElement add(Throwable err, boolean rethrow) {
		var errN = new ExceptionElement(this, null);
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
