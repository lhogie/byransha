package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.ListNode;
import byransha.BBGraph;

public class Building extends BNode {
	public  ListNode<Office> offices;

	public Building(BBGraph g) {
		super(g);
		offices = new ListNode<Office>(g);
	}

	public Building(BBGraph g, int id){
		super(g, id);
	}

//	ListNode<Building> buildings;
	public Office findOffice(String name) {
		for (var o : offices.l) {
			if (o.name.get().equals(name)) {
				return o;
			}
		}

		return null;
	}


	
	@Override
	public String prettyName() {
		return "building";
	}

	
	@Override
	public String whatIsThis() {
		return "Building description";
	}
}
