package byransha.nodes.lab;

import java.util.List;

import byransha.graph.BNode;

public class PhDStudent extends Position {
	List<Person> directors;
	Structure team;

	public PhDStudent(BNode g) {
		super(g);
	}

}
