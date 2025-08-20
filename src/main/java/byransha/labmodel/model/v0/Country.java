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
		this.setColor("#fc0307", creator);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		codeNode = new StringNode(g, creator, InstantiationInfo.persisting);
		name = new StringNode(g, creator, InstantiationInfo.persisting);
		flag = new Out<>(g, creator, InstantiationInfo.persisting);
	}

	public void setFlagCode(String code, User user) throws IOException {
		codeNode.set(code, user);
		name.set(countryCodes.get(code).asText(), user);

		try {
			flag.get().data.set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes(), user);
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
					country.flag.get().title.set(countryCode, creator);
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
		if(name == null || name.get() == null || name.get().isEmpty() ||codeNode.get() == null || codeNode.get().isEmpty()) return "Country(unknown)";
		return name.get() + "(" + codeNode.get().toUpperCase() + ")";
	}
}
