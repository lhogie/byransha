package byransha.labmodel.model.v0;

import java.io.IOException;
import java.util.Objects;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Country extends BusinessNode {
	static JsonNode countryCodes;

	private Out<StringNode> name, codeNode;
	private Out<DocumentNode> flag;

	public Country(BBGraph g, String code, User creator) {
		super(g, creator);
		codeNode = new Out<>(g, creator);
		codeNode.get().set(code, creator);
		name = new Out<>(g, creator);
		name.fromString(countryCodes.get(code).asText(), creator);
		flag = new Out<>(g, creator);

		try {
			flag.get().set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
					.readAllBytes(), creator);
		} catch (IOException err) {
			err.printStackTrace();
		}

		endOfConstructor();
	}

	public Country(BBGraph g, User creator) {
        super(g, creator);
        codeNode = new Out<>(g, creator);
		name = new Out<>(g, creator);
		flag = new  Out<>(g, creator);
		this.setColor("#fc0307", creator);
		endOfConstructor();
	}

	public void setFlagCode(String code, User user) throws IOException {
		flag.get().set(Country.class.getResource("/country_flags/svg/" + code.toLowerCase() + ".svg").openStream()
				.readAllBytes(), user);
		codeNode.get().set(code, user);
		name.get().set(countryCodes.get(code).asText(), user);
	}

	public Country(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

	public static void loadCountries(BBGraph g, User creator) {
		var res = Country.class.getResource("/country_flags/countries.json");
		Objects.requireNonNull(res);

		try (var stream = res.openStream()){
			var json = new String(stream.readAllBytes());
			countryCodes = new ObjectMapper().readTree(json);

			countryCodes.fieldNames().forEachRemaining(code -> {
				var country = new Country(g, creator);
				try {
					country.flag.get().title.set(countryCodes.get(code).asText(), creator);
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
		if(name == null || name.get().get().isEmpty() ||codeNode.get() == null || codeNode.get().get().isEmpty()) return "Country(unknown)";
		return name.get() + "(" + codeNode.get().get().toUpperCase() + ")";
	}
}
