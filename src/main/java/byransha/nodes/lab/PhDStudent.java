package byransha.nodes.lab;

import java.util.List;

import byransha.graph.BGraph;
import byransha.nodes.system.User;

public class PhDStudent extends Position {
	public PhDStudent(BGraph g) {
		super(g);
	}

	List<Person> directors;
	Structure team;
}
