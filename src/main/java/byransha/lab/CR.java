package byransha.lab;

import byransha.ID;
import byransha.graph.Element;

public class CR extends Status {
	public CR(Element g, ID id) {
		super(g, id);
		name.set("Chargé de Recherche");
		name.userEditable = true;
	}
}
