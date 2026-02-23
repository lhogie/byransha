package byransha.nodes.lab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.DocumentNode;
import byransha.nodes.system.User;

public class DataLake extends BNode {

	public final File dir;

	public DataLake(BBGraph g, File dir) {
		super(g);
		this.dir = dir;
	}

	static JsonNode countryCodes;

	public void loadCountries(BBGraph g) throws IOException {

		var json = Files.readAllBytes(new File(dir, "country_flags/countries.json").toPath());
		countryCodes = new ObjectMapper().readTree(json);

		countryCodes.fieldNames().forEachRemaining(code -> {
			var country = new Country(g);
			country.code = code;

			country.name = countryCodes.get(code).asText();
			country.flag = new DocumentNode(g);

			try {
				var fileFlag = new File(dir, "country_flags/svg/" + code.toLowerCase() + ".svg");
				country.flag.data.set(Files.readAllBytes(fileFlag.toPath()));
				country.flag.mimeType.set("image/svg+xml");
				country.flag.title.set(country.name + ".svg");
			} catch (IOException err) {
				throw new RuntimeException(err);
			}
		});
	}

	static DateTimeFormatter[] formatters = new DateTimeFormatter[] { DateTimeFormatter.ofPattern("dd/MM/yyyy"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("MM/dd/yyyy"),
			DateTimeFormatter.ofPattern("yyyy") };

	public static OffsetDateTime parseDate(String date) {
		if (date == null || date.isBlank() || date.equals("0000-00-00") || date.equals("1999-00-00")) {
			return null;
		}

		if (date.matches("\\d{4}")) {
			date += "-01-01";
		}

		var zone = ZoneId.of("Europe/Paris");

		for (var f : formatters) {
			try {
				return LocalDate.parse(date, f).atStartOfDay(zone).toOffsetDateTime();
			} catch (DateTimeParseException e) {
				// try next formatter
			}
		}

		throw new DateTimeParseException("unknown date format " + date, date, 0);
	}

	@Override
	public String whatIsThis() {
		return "a data lake";
	}

	@Override
	public String prettyName() {
		return "datalake at " + dir.getAbsolutePath();
	}

	public void load() throws IOException {
		if (dir == null)
			throw new NullPointerException();

		if (!dir.exists() || !dir.isDirectory())
			throw new IOException("Input directory does not exist or not a directory: " + dir);

		System.out.println("Loading datalake from " + dir);
		loadCountries(g);

		User user = g.systemUser;

		Files.readAllLines(new File(dir, "CH_Nationality_List_20171130_v1.csv").toPath()).forEach(l -> {
			var c = new Nationality(g);
			c.set(l);
		});

		Lab i3s = new Lab(g);

		for (var n : List.of("CNRS", "Inria")) {
			var epst = new EPST(g);
			epst.name.set(n);
			i3s.tutelles.add(epst);
		}

		var UniCA = new University(g); // new University(graph);
		UniCA.name.set("UniCA");
		i3s.tutelles.add(UniCA);

		for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
			var group = new ResearchGroup(g); // new ResearchGroup(graph);
			group.name.set(n);
			i3s.subStructures.add(group);
		}

		for (var n : List.of("ALGORITHMES", "Inria", "IUT Sophia", "Polytech", "Lucioles", "Valrose", "Fabron")) {
			var campus = new Campus(g); // new Campus(graph);
			campus.name.set(n);
			UniCA.campuses.add(campus);
		}

		new OldTBRH().loadOLDTBRH(i3s, new File(dir, "i3s/tbrh"));

		System.out.println(" Finished loading datalake");
	}

}
