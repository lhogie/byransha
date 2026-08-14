package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;

public class CR extends Status {
	public CR(InstantiationParameter p) {
		super(p);
		name.set("Chargé de Recherche");
		name.userEditable = true;
	}
}
