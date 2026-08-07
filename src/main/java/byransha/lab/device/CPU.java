package byransha.lab.device;

import byransha.graph.BNode;
import byransha.primitive.LongNode;
import byransha.primitive.URLNode;

public class CPU extends BNode {
	LongNode nbCores;
	URLNode url;

	public CPU(BNode parent) {
		super(parent);
	}

}
