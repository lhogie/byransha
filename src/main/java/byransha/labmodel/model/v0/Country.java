package byransha.labmodel.model.v0;

import java.io.IOException;
import java.util.Objects;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Country extends BusinessNode {
	static JsonNode countryCodes;

	private StringNode name, codeNode;
	private Out<DocumentNode> flag;

	public Country(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		codeNode = new StringNode(g, creator, InstantiationInfo.persisting);
		name = new StringNode(g, creator, InstantiationInfo.persisting);
		flag = new Out<>(g, creator, InstantiationInfo.persisting);
		this.setColor("#fc0307", creator);
	}

	public void setFlagCode(String code, User user) throws IOException {
		codeNode.set(code, user);
		name.set(countryCodes.get(code).asText(), user);

		try {
			flag.get().data.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes(), user);
			flag.get().mimeType.set("image/svg+xml", user);
			flag.get().title.set(name.get() + ".svg", user);
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	public static void loadCountries(BBGraph g, User creator) {
		var res = Country.class.getResource("/country_flags/countries.json");
		Objects.requireNonNull(res);

		try (var stream = res.openStream()){
			var json = new String(stream.readAllBytes());
			countryCodes = new ObjectMapper().readTree(json);

			countryCodes.fieldNames().forEachRemaining(code -> {
				var country = new Country(g, creator,  InstantiationInfo.persisting);

				try {
					var countryCode = countryCodes.get(code).asText();
					var doc = new DocumentNode(g, creator, InstantiationInfo.persisting);
					country.flag.set(doc, creator);
					country.setFlagCode(code, creator);
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
