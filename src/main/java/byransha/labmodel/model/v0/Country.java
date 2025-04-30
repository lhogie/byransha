package byransha.labmodel.model.v0;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.BBGraph;
import byransha.ImageNode;
import byransha.StringNode;

public class Country extends BusinessNode {
	StringNode name, codeNode;
	ImageNode flag;
	static JsonNode countryCodes;

	public static void loadCountries(BBGraph g) {
		try {
			var res = Country.class.getResource("/country_flags/countries.json");
			var json = new String(res.openStream().readAllBytes());
			countryCodes = new ObjectMapper().readTree(json);

			countryCodes.fieldNames().forEachRemaining(code -> {
				new Country(g, code);
			});
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public Country(BBGraph g, String code) {
		super(g);
		codeNode = new StringNode(g);
		codeNode.set(code);
		name = new StringNode(g, countryCodes.get(code).asText());
		flag = new ImageNode(g);

		try {
			flag.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes());
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	public Country(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a country";
	}

	@Override
	public String prettyName() {
		return name.get() + "(" + codeNode.get().toUpperCase() + ")";
	}
}
