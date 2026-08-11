package byransha.lab;

import byransha.Element;
import byransha.ID;

public class CR extends Status {
	public CR(Element g, ID id) {
		super(g, id);
		name.set("Chargé de Recherche");
		name.userEditable = true;
	}
}
