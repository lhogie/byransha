package byransha.nodes.lab;

import java.io.IOException;
import java.util.Objects;

import byransha.nodes.system.User;
import byransha.graph.BBGraph;
import byransha.graph.DocumentNode;
import byransha.nodes.primitive.StringNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Country extends BusinessNode {
	static JsonNode countryCodes;

	private StringNode name, codeNode;
	private DocumentNode flag;

	public Country(BBGraph g) {
        super(g);
		codeNode = new StringNode(g);
		name = new StringNode(g);
	}


	public void setFlagCode(String code) throws IOException {
		codeNode.set(code);
		name.set(countryCodes.get(code).asText());

		try {
			flag.data.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes());
			flag.mimeType.set("image/svg+xml");
			flag.title.set(name.get() + ".svg");
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	public static void loadCountries(BBGraph g) {
		var res = Country.class.getResource("/country_flags/countries.json");
		Objects.requireNonNull(res);

		try (var stream = res.openStream()){
			var json = new String(stream.readAllBytes());
			countryCodes = new ObjectMapper().readTree(json);

			countryCodes.fieldNames().forEachRemaining(code -> {
				var country = new Country(g);

				try {
					var countryCode = countryCodes.get(code).asText();
					country.flag= new DocumentNode(g);
					country.setFlagCode(code);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			});
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	@Override
	public String whatIsThis() {
		return "a country";
	}

	@Override
	public String prettyName() {
		if(name != null || codeNode != null ) {
			String pretty = "";
			if(name != null && name.get() != null && !name.get().isBlank()) {
				pretty += name.get();
			}
			if(codeNode != null && codeNode.get() != null && !codeNode.get().isBlank()) {
				if(!pretty.isBlank()) { pretty += " "; }
				pretty += "(" + codeNode.get().toUpperCase() + ")";
			}
			if(!pretty.isBlank()) { return pretty; }
		}
		return null;

	}
}
