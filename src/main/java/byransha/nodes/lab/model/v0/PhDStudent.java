package byransha.nodes.lab.model.v0;

import java.util.List;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class PhDStudent extends Position {
	public PhDStudent(BBGraph g) {
		super(g);
	}

	List<Person> directors;
	Structure team;
}
