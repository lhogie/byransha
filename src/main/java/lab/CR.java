package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;

public class CR extends Status {
	public CR(InstantiationParameters p) {
		super(p);
		name.set("Chargé de Recherche");
		name.userEditable = true;
	}
}
