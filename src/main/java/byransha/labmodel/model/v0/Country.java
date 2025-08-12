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

	public static void loadCountries(BBGraph g, User creator) {
		try {
			var res = Country.class.getResource("/country_flags/countries.json");
			var json = new String(res.openStream().readAllBytes());
			countryCodes = new ObjectMapper().readTree(json);

			countryCodes.fieldNames().forEachRemaining(code -> {
                var country = new Country(g, creator);
                try {
					country.flag.title.set(countryCodes.get(code).asText(), creator);
                    country.setFlagCode(code, creator);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
			});
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public Country(BBGraph g, String code, User creator) {
		super(g, creator);
		codeNode = new StringNode(g, creator);
		codeNode.set(code, creator);
		name = new StringNode(g, creator, countryCodes.get(code).asText());
		flag = new ImageNode(g, creator);

		try {
			flag.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes(), creator);
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	public Country(BBGraph g, User creator) {
        super(g, creator);
        codeNode = new  StringNode(g, creator);
		name = new  StringNode(g, creator);
		flag = new  ImageNode(g, creator);
		this.setColor("#fc0307", creator);
	}

	public void setFlagCode(String code, User user) throws IOException {
		flag.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
				.readAllBytes(), user);
		flag.setMimeType("image/svg+xml");
		codeNode.set(code, user);
		name =new  StringNode(graph, user); //new StringNode(graph, countryCodes.get(code).asText());
		name.set(countryCodes.get(code).asText(), user);
	}

	public Country(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}

	@Override
	public String whatIsThis() {
		return "a country";
	}

	@Override
	public String prettyName() {
		if(name == null || name.get().isEmpty() ||codeNode.get() == null || codeNode.get().isEmpty()) return "Country(unknown)";
		return name.get() + "(" + codeNode.get().toUpperCase() + ")";
	}
}
