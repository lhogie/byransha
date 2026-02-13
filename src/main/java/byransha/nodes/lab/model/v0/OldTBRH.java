package byransha.nodes.lab.model.v0;

import java.io.File;
import java.io.IOException;
import java.util.List;

import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.EmailNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;
import toools.Stop;

class OldTBRH {
	public void loadOLDTBRH(Lab i3s, User user, File inputDir) throws IOException {
		loadPersonnel(i3s, user, inputDir);
	}

	private static void loadPersonnel(Lab i3s, User user, File extractionDir) throws IOException {

		var csv = new CSV(new File(extractionDir, "personneI3S_IT.csv"), ";");

		int total = csv.size();
		int barLength = 40;
		int count = 0;
		;

		for (var l : csv) {
			var person = new Person(i3s.g, user); // new Person(graph);

			if (l.set(0, null).equals("member")) {
				var position = new Position(i3s.g, user);
				position.employer = i3s;
				person.positions.add(position, user);
			}

			person.etatCivil.name.set(l.set(1, null), user);
			person.etatCivil.familyNameBeforeMariage.set(l.set(2, null), user);
			person.etatCivil.firstName.set(l.set(3, null), user);
			person.etatCivil.birthDate.set(DataLake.parseDate(l.set(4, null)), user);
			person.etatCivil.cityOfBirth.set(l.set(5, null), user);
//            person.etatCivil.countryOfBirth.set(l.set(6, null));
//            person.etatCivil.nationality.set(l.set(7, null));
			person.etatCivil.address.set(l.set(8, null), user);
			var inter = new StringNode(i3s.g, user);
			inter.set(l.set(9, null), user);
			person.phoneNumbers.add(inter, user);

			var officeName = l.set(15, null);

			for (var campusName : List.of(l.set(10, null), l.set(11, null))) {
				if (!campusName.isBlank()) {
					var campus = i3s.g.forEachNodeOfClass(Campus.class,
							n -> Stop.stopIf(n.name.get() != null && n.name.get().equalsIgnoreCase(campusName)));

					if (campus != null && !officeName.isBlank()) {
						for (var b : campus.buildings.getElements()) {
							var office = b.findOffice(officeName);

							if (office != null) {
								person.offices.add((Office) office, user);
							}
						}
					}
				}
			}

			for (var phoneNumber : List.of(l.set(12, null), l.set(13, null), l.set(14, null))) {
				var n = new StringNode(i3s.g, user); // new StringNode(this, phoneNumber);
				n.set(phoneNumber, user);
				person.phoneNumbers.add(n, user);
			}

			person.badgeNumber.set(l.set(16, null), user);
			person.website.set(l.set(17, null), user);
			person.faxNumber.set(l.set(18, null), user);
			var email = new EmailNode(i3s.g, user, null);
			email.set(l.set(19, null), user);
			person.emailAddresses.add(email, user);
			String researchGroupName = l.set(20, null);
			person.researchGroup = i3s.g.forEachNodeOfClass(ResearchGroup.class,
					n -> Stop.stopIf(n.name.get() != null && n.name.get().equalsIgnoreCase(researchGroupName)));
			boolean doctor = l.set(21, null).equalsIgnoreCase("oui");
			String phdDate = l.set(22, null);

			if (phdDate != null && phdDate != "") {
				person.phdDate.set(DataLake.parseDate(phdDate), user);
			} else if (doctor) {
				person.phdDate.set(null, user);
			}
			// System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
			var startDate = l.set(30, null);
			var endDate = l.set(31, null);

			for (var i : List.of(25, 26)) {
				var employer = l.set(i, null);
				person.position = new Position(i3s.g, user); // new Position(graph);
				person.position.employer = i3s.g.forEachNodeOfClass(ResearchGroup.class,
						n -> Stop.stopIf(n.name.get() != null && n.name.get().equals(employer)));

				var corps = l.set(i - 2, null);
//                person.position.status = graph.find(Status(g, user), s ->
//                    s.name.get().equals(corps)
//                );

				if (!startDate.isBlank()) {
					var startDateNode = new DateNode(i3s.g, user);
					startDateNode.set(DataLake.parseDate(startDate), user);
					person.position.from = startDateNode;
				}

				if (!endDate.isBlank()) {
					var endDateNode = new DateNode(i3s.g, user);
					endDateNode.set(DataLake.parseDate(endDate), user);
					person.position.to = endDateNode;
				}
			}

			person.enposte = l.set(27, null).equals("en poste");
			var quotite = new StringNode(i3s.g, user);
			quotite.set(l.set(29, null), user);
			person.quotite = quotite;
			var researchActivity = new StringNode(i3s.g, user);
			researchActivity.set(l.set(33, null), user);
			person.researchActivity = researchActivity;

//            if (
//                l.stream().anyMatch(Objects::nonNull)
//            ) throw new IllegalStateException("unused columns: " + l);

			// ---- Progression ----
			count++;
			int percent = (count * 100) / total;
			int filled = (count * barLength) / total;

			String bar = "[" + "=".repeat(filled) + " ".repeat(barLength - filled) + "]";
			System.out.print("\r" + bar + " " + percent + "% (" + count + "/" + total + ")");
		}

	}
}
