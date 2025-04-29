package byransha.labmodel.model.v0;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import byransha.BBGraph;
import byransha.DateNode;
import byransha.EmailNode;
import byransha.StringNode;
import byransha.labmodel.model.v0.datalake.CSV;
import toools.io.file.RegularFile;

public class AcademiaDB extends BBGraph {
	public static void main(String[] args) throws Exception {
		var db = new AcademiaDB();
		db.loadFromLake();
	}

	public AcademiaDB() {
		this(null);
	}

	public AcademiaDB(File directory) {
		super(directory);
	}

	public void loadFromLake() throws IOException {
		loadFromLake(new RegularFile("~/perso/local_files/job/i3s/tableau_de_bord/input_sources").toFile());
	}

	public void loadFromLake(File inputDir) throws IOException {
		Files.readAllLines(new File(inputDir, "CH_Nationality_List_20171130_v1.csv").toPath()).forEach(l -> {
			var c = new Country(graph);
			c.name.set(l);
		});

		System.out.println(graph + " " + this);
		Lab i3s = new Lab(graph);

		for (var n : List.of("CNRS", "Inria")) {
			var epst = new EPST(graph);
			epst.name.set(n);
			i3s.tutelles.add(epst);
		}

		var UniCA = new University(graph);
		UniCA.name.set("UniCA");
		i3s.tutelles.add(UniCA);

		for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
			var group = new ResearchGroup(graph);
			group.name.set(n);
			i3s.subStructures.add(group);
		}

		for (var n : List.of("ALGORITHMES", "Inria", "IUT Sophia", "Polytech", "Lucioles", "Valrose", "Fabron")) {
			var campus = new Campus(graph);
			campus.name.set(n);
			UniCA.campuses.add(campus);
		}

		var csv = new CSV(new File(inputDir, "TB_personneI3S_IT.csv"), ";");

		for (var l : csv) {
			var p = new Person(graph);

			if (l.set(0, null).equals("member")) {
				i3s.members.add(p);
			}

			p.etatCivil.name.set(l.set(1, null));
			p.etatCivil.familyNameBeforeMariage.set(l.set(2, null));
			p.etatCivil.firstName.set(l.set(3, null));
			p.etatCivil.birthDate.set(l.set(4, null));
			p.etatCivil.cityOfBirth.set(l.set(5, null));
			p.etatCivil.countryOfBirth.set(l.set(6, null));
			p.etatCivil.nationality.set(l.set(7, null));
			p.etatCivil.address.set(l.set(8, null));
			p.phoneNumbers.add(new StringNode(this, l.set(9, null)));

			var officeName = l.set(15, null);

			for (var campusName : List.of(l.set(10, null), l.set(11, null))) {
				if (!campusName.isBlank()) {
					var campus = find(Campus.class, n -> n.name.get().equalsIgnoreCase(campusName));

					if (campus != null && !officeName.isBlank()) {
						var office = campus.findOffice(officeName);

						if (office != null) {
							p.offices.add(office);
						}
					}
				}
			}

			for (var phoneNumber : List.of(l.set(12, null), l.set(13, null), l.set(14, null))) {
				var n = new StringNode(this, phoneNumber);
				p.phoneNumbers.add(n);
			}

			p.badgeNumber.set(l.set(16, null));
			p.website.set(l.set(17, null));
			p.faxNumber.set(l.set(18, null));
			p.emailAddresses.add(new EmailNode(this, l.set(19, null)));
			p.researchGroup = find(ResearchGroup.class, n -> n.name.get().equals(l.set(20, null)));
			boolean doctor = l.set(21, null).equalsIgnoreCase("oui");
			String phdDate = l.set(22, null);

			if (phdDate != null) {
				p.phdDate.set(phdDate);
			} else if (doctor) {
				p.phdDate.set("unknown");
			}
//			System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
			var startDate = l.set(30, null);
			var endDate = l.set(31, null);

			for (var i : List.of(25, 26)) {
				var employer = l.set(i, null);
				p.position = new Position(graph);
				p.position.employer = find(ResearchGroup.class, n -> n.name.get().equals(employer));
				var corps = l.set(i - 2, null);
				p.position.status = find(Status.class, s -> s.name.get().equals(corps));

				if (!startDate.isBlank()) {
					p.position.from = new DateNode(this, startDate);
				}

				if (!endDate.isBlank()) {
					p.position.to = new DateNode(this, endDate);
				}
			}

			p.enposte = l.set(27, null).equals("en poste");
			p.position.comment = l.set(28, null);
			p.quotite = new StringNode(this, l.set(29, null));
			comment = l.set(32, null);
			p.researchActivity = new StringNode(this, l.set(33, null));

			if (l.stream().anyMatch(Objects::nonNull))
				throw new IllegalStateException("unused columns: " + l);
		}
	}

}
