package byransha.nodes.lab;

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
