package byransha.labmodel.model.v0;

import java.awt.*;
import java.io.IOException;
import java.util.Base64;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                var country = BNode.create(g, Country.class);
                try {
					country.flag.title.set(countryCodes.get(code).asText());
                    country.setFlagCode(code);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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

	public Country(BBGraph g) {
        super(g);
        codeNode = BNode.create(g, StringNode.class);
		name = BNode.create(g, StringNode.class);
		flag = BNode.create(g, ImageNode.class);
		this.setColor("#fc0307");
	}

	public void setFlagCode(String code) throws IOException {
		flag.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
				.readAllBytes());
		flag.setMimeType("image/svg+xml");
		codeNode.set(code);
		name = BNode.create(graph, StringNode.class); //new StringNode(graph, countryCodes.get(code).asText());
		name.set(countryCodes.get(code).asText());
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
		if(name.get() == null || name.get().isEmpty() ||codeNode.get() == null || codeNode.get().isEmpty()) {

				System.err.println("Country with no name and code: " + this);
				return "Country(unknown)";

		}
		return name.get() + "(" + codeNode.get().toUpperCase() + ")";
	}
}
