package byransha.nodes.lab;

import java.io.File;
import java.io.IOException;
import java.util.List;

import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.EmailNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.PhoneNumberNode;
import byransha.nodes.primitive.StringNode;
import byransha.util.Stop;

class OldTBRH {
	public void loadOLDTBRH(Lab i3s, File inputDir) throws IOException {
		loadPersonnel(i3s, inputDir);
	}

	private static void loadPersonnel(Lab i3s, File extractionDir) throws IOException {

		var csv = new CSV(new File(extractionDir, "personneI3S_IT.csv"), ";");

		for (var l : csv) {
			var person = new Person(i3s.g); // new Person(graph);

			if (l.set(0, null).equals("member")) {
				var position = new Position(i3s.g);
				position.employer = i3s;
				person.positions.elements.add(position);
			}

			person.etatCivil.name.set(l.set(1, null));
			person.etatCivil.familyNameBeforeMariage.set(l.set(2, null));
			person.etatCivil.firstName.set(l.set(3, null));
			person.etatCivil.birthDate.set(DataLake.parseDate(l.set(4, null)));
			person.etatCivil.cityOfBirth.set(l.set(5, null));
//            person.etatCivil.countryOfBirth.set(l.set(6, null));
//            person.etatCivil.nationality.set(l.set(7, null));
			person.etatCivil.address.set(l.set(8, null));
			var inter = new PhoneNumberNode(i3s.g);
			inter.set(l.set(9, null));
			person.phoneNumbers.elements.add(inter);

			var officeName = l.set(15, null);

			for (var campusName : List.of(l.set(10, null), l.set(11, null))) {
				if (!campusName.isBlank()) {
					var campus = i3s.g.indexes.byClass.forEachNodeAssignableTo(Campus.class,
							n -> Stop.stopIf(n.name.get() != null && n.name.get().equalsIgnoreCase(campusName)));

					if (campus != null && !officeName.isBlank()) {
						for (var b : campus.buildings.elements) {
							var office = b.findOffice(officeName);

							if (office != null) {
								person.offices.elements.add((Office) office);
							}
						}
					}
				}
			}

			for (var phoneNumber : List.of(l.set(12, null), l.set(13, null), l.set(14, null))) {
				var n = new PhoneNumberNode(i3s.g);
				n.set(phoneNumber);
				person.phoneNumbers.elements.add(n);
			}

			person.badgeNumber.set(l.set(16, null));
			person.website.set(l.set(17, null));
			person.faxNumber.set(l.set(18, null));
			var email = new EmailNode(i3s.g, null);
			email.set(l.set(19, null));
			person.emailAddresses.elements.add(email);
			String researchGroupName = l.set(20, null);
			person.structures.elements.add(i3s.g.indexes.byClass.forEachNodeAssignableTo(ResearchGroup.class,
					n -> Stop.stopIf(n.name.get() != null && n.name.get().equalsIgnoreCase(researchGroupName))));
			boolean doctor = l.set(21, null).equalsIgnoreCase("oui");
			String phdDate = l.set(22, null);

			if (phdDate != null && phdDate != "") {
				person.phdDate.set(DataLake.parseDate(phdDate));
			} else if (doctor) {
				person.phdDate.set(null);
			}
			// System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
			var startDate = l.set(30, null);
			var endDate = l.set(31, null);

			for (var i : List.of(25, 26)) {
				var employer = l.set(i, null);
				person.position = new Position(i3s.g); // new Position(graph);
				person.position.employer = i3s.g.indexes.byClass.forEachNodeAssignableTo(ResearchGroup.class,
						n -> Stop.stopIf(n.name.get() != null && n.name.get().equals(employer)));

				var corps = l.set(i - 2, null);
//                person.position.status = graph.find(Status(g), s ->
//                    s.name.get().equals(corps)
//                );

				if (!startDate.isBlank()) {
					var startDateNode = new DateNode(i3s.g);
					startDateNode.set(DataLake.parseDate(startDate));
					person.position.from = startDateNode;
				}

				if (!endDate.isBlank()) {
					var endDateNode = new DateNode(i3s.g);
					endDateNode.set(DataLake.parseDate(endDate));
					person.position.to = endDateNode;
				}
			}

			person.enposte = l.set(27, null).equals("en poste");

			try {
				var quotite = new LongNode(i3s.g);
				quotite.set(Long.valueOf(l.set(29, null)));
				person.quotite = quotite;
			} catch (NumberFormatException err) {

			}

			var researchActivity = new StringNode(i3s.g);
			researchActivity.set(l.set(33, null));
			person.researchActivity = researchActivity;

		}

	}
}
