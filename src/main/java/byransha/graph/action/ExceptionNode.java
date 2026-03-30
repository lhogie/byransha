package byransha.graph.action;

import java.time.LocalDateTime;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class ExceptionNode extends BNode {
	public Throwable err;
	public LocalDateTime date;

	public ExceptionNode(BGraph g) {
		super(g);
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
