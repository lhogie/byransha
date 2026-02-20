package byransha.graph.action;

import java.time.LocalDateTime;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class ExceptionNode extends BNode {
	public Throwable err;
	public LocalDateTime date;

	public ExceptionNode(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "an error";
	}

	@Override
	public String prettyName() {
		return err.getMessage();
	}

}
