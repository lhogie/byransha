package lab;

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

import byransha.Element;
import byransha.action.ActionMethod;
import byransha.action.base.ShowInKishanView;
import byransha.nodes.primitive.file.FileNode;
import byransha.service.system.Hub;
import byransha.util.Cout;

public class DataLake extends Element {
	@ShowInKishanView
	public final FileNode dir;

	public DataLake(Element g, File dir) {
		super(g, null);
		this.dir = new FileNode(g, null);
		this.dir.file = dir;

		if (!dir.exists())
			throw new IllegalArgumentException("data lake not found at " + dir.getAbsolutePath());
	}


	static JsonNode countryCodes;

	public static void loadCountries(Hub hub, File dataLakeDir) throws IOException {
		var dir = new File(dataLakeDir, "country_flags");
		var json = Files.readAllBytes(new File(dir, "countries.json").toPath());
		countryCodes = new ObjectMapper().readTree(json);

		countryCodes.fieldNames().forEachRemaining(code -> {
			Country country = hub.fieldNode("country-" + code, id -> new Country(hub, id));
			country.code = code;
			country.name = countryCodes.get(code).asText();

			try {
				var fileFlag = new File(dir, "svg/" + code.toLowerCase() + ".svg");
				country.flag.set(Files.readAllBytes(fileFlag.toPath()));
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
	public String toString() {
		return "datalake at " + dir.file.getAbsolutePath();
	}

	@ActionMethod
	public void load() throws IOException {
		var app = (LabApplication) parent;
		var i3s = app.i3s;

		Cout.progress("Loading datalake from " + dir);
		loadCountries(hub(), dir.file);

		ACMClassifier.load(hub(), dir.file);

		Cout.progress("\tLoading nationalities");
		Files.readAllLines(new File(dir.file, "CH_Nationality_List_20171130_v1.csv").toPath()).forEach(l -> {
//			var c = hub().lookupOrCreate("nationality- + l", id -> new Nationality(hub()));
//			c.set(l);
		});

		var france = hub().indexes.byClass.findFirst(Country.class, c -> c.name.equals("France"));

		for (var n : List.of("CNRS", "Inria")) {
			var epst = app.fieldNode("nationality- + l", id -> new EPST(france, id));
			epst.name.set(n);
			i3s.tutelles.elements.add(epst);
		}

		var unica = app.fieldNode("unica", id -> new University(hub(), id)); // new University(graph);
		unica.name.set("UniCA");
		i3s.tutelles.elements.add(unica);

		for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
			var group = app.fieldNode(n, id -> new ResearchGroup(i3s, id, n));
			i3s.subStructures.elements.add(group);
		}

		var adminGroup = app.fieldNode("admin", id -> new Structure(i3s, id));
		adminGroup.name.set("SG/Administration");
		i3s.subStructures.elements.add(adminGroup);

		var infoGroup = app.fieldNode("info", id -> new Structure(i3s, id));
		infoGroup.name.set("SG/Informatique");
		i3s.subStructures.elements.add(infoGroup);

		var ds4h = unica.fieldNode("ds4h", id -> new EUR(i3s, id));
		ds4h.name.set("DS4H");
		i3s.subStructures.elements.add(ds4h);

		for (var n : List.of("ALGORITHMES", "Inria", "IUT Sophia", "Polytech", "Lucioles", "Valrose", "Fabron")) {
			var campus = unica.fieldNode(n, id -> new Campus(unica, id)); // new Campus(graph);
			campus.name.set(n);
			unica.campuses.elements.add(campus);
		}

		Cout.progress("\tLoading old TBRH");
		new OldTBRH().loadOLDTBRH(i3s, new File(dir.file, "i3s/tbrh"));

		Cout.progress("End loading");

	}

}
