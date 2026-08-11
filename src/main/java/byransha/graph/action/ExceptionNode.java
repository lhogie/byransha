package byransha.graph.action;

import java.time.LocalDateTime;

import byransha.ID;
import byransha.graph.Element;

public class ExceptionNode extends Element {
	public Throwable err;
	public LocalDateTime date;

	public ExceptionNode(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "an error";
	}

	@Override
	public String toString() {
		return err.getMessage();
	}

}
