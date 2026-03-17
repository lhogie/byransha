package byransha.nodes.lab;

import java.util.List;

import byransha.graph.BGraph;

public class PhDStudent extends Position {
	List<Person> directors;
	Structure team;

	public PhDStudent(BGraph g) {
		super(g);
	}

}
