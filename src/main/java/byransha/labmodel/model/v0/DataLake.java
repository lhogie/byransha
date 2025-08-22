package byransha.labmodel.model.v0;

import byransha.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataLake extends BNode {

    public final File inputDir;

    public DataLake(BBGraph g, User creator,File dir) {
        super(g, creator, InstantiationInfo.notPersisting);
        inputDir = dir;
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    @Override
    public String whatIsThis() {
        return "a data lake";
    }

    @Override
    public String prettyName() {
        return "I3S datalake";
    }

    public OffsetDateTime parseDate(String date) {
        if (date == null || date.isBlank() || date.equals("0000-00-00") || date.equals("1999-00-00")) {
            return null;
        }
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(date, formatter1)
                    .atStartOfDay(ZoneId.of("Europe/Paris")).toOffsetDateTime();
        } catch (Exception e) {
            try {
                return LocalDate.parse(date, formatter2)
                        .atStartOfDay(ZoneId.of("Europe/Paris")).toOffsetDateTime();
            } catch (Exception e2) {
                if (date.matches("\\d{4}")) {
                    return LocalDate.parse(date + "-01-01", formatter2)
                            .atStartOfDay(ZoneId.of("Europe/Paris")).toOffsetDateTime();
                }
                throw new IllegalArgumentException("Invalid date format: " + date, e2);
            }
        }
    }

    public void load() throws IOException {
        System.out.println("Loading datalake from " + inputDir);
        User user = g.systemUser();

        if(inputDir == null) {return;}
        else if(!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IOException("Input directory does not exist or not a directory: " + inputDir);
        }

        Files.readAllLines(
            new File(inputDir, "CH_Nationality_List_20171130_v1.csv").toPath()
        ).forEach(l -> {
                var c = new Nationality(g, user, InstantiationInfo.persisting);
                c.set(l, user);
            });

        Lab i3s = new Lab(g, user, InstantiationInfo.persisting);

        for (var n : List.of("CNRS", "Inria")) {
            var epst = new EPST(g, user, InstantiationInfo.persisting); //new EPST(graph);
            epst.name.set(n, user);
            i3s.tutelles.add(epst, user);
        }

        var UniCA = new University(g, user, InstantiationInfo.persisting); //new University(graph);
        UniCA.name.set("UniCA", user);
        i3s.tutelles.add(UniCA, user);

        for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
            var group = new ResearchGroup(g, user, InstantiationInfo.persisting); //new ResearchGroup(graph);
            group.name.set(n, user);
            i3s.subStructures.add(group, user);
        }

        for (var n : List.of(
            "ALGORITHMES",
            "Inria",
            "IUT Sophia",
            "Polytech",
            "Lucioles",
            "Valrose",
            "Fabron"
        )) {
            var campus = new Campus(g, user, InstantiationInfo.persisting); //new Campus(graph);
            campus.name.set(n, user);
            UniCA.campuses.add(campus, user);
        }

        var upToDateDir = new File(inputDir, "marijo_tableau_de_bord");
        var oldDir = new File(upToDateDir, "old");

        var csv = new CSV(new File(oldDir, "TB_personneI3S_IT.csv"), ";");

        for (var l : csv) {
            var person = new Person(g, user, InstantiationInfo.persisting); //new Person(graph);

            if (l.set(0, null).equals("member")) {
                var position = new Position(g, user, InstantiationInfo.persisting);
                position.employer = i3s;
                person.positions.add(position, user);
            }

            person.etatCivil.name.set(l.set(1, null), user);
            person.etatCivil.familyNameBeforeMariage.set(l.set(2, null), user);
            person.etatCivil.firstName.set(l.set(3, null), user);
            person.etatCivil.birthDate.set(parseDate(l.set(4, null)), user);
            person.etatCivil.cityOfBirth.set(l.set(5, null), user);
//            person.etatCivil.countryOfBirth.set(l.set(6, null));
//            person.etatCivil.nationality.set(l.set(7, null));
            person.etatCivil.address.set(l.set(8, null), user);
            var inter = new StringNode(g, user, InstantiationInfo.persisting);
            inter.set(l.set(9, null), user);
            person.phoneNumbers.add(inter, user);

            var officeName = l.set(15, null);

            for (var campusName : List.of(l.set(10, null), l.set(11, null))) {
                if (!campusName.isBlank()) {
                    var campus = g.find(Campus.class, n -> {
                            if (n.name != null && n.name.get() != null) {
                                n.name.get().equalsIgnoreCase(campusName);
                            }
                                return false;
                            }
                    );

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

            for (var phoneNumber : List.of(
                l.set(12, null),
                l.set(13, null),
                l.set(14, null)
            )) {
                var n = new StringNode(g, user, InstantiationInfo.persisting); //new StringNode(this, phoneNumber);
                n.set(phoneNumber, user);
                person.phoneNumbers.add(n, user);
            }

            person.badgeNumber.set(l.set(16, null), user);
            person.website.set(l.set(17, null), user);
            person.faxNumber.set(l.set(18, null), user);
            var email = new EmailNode(g, user, InstantiationInfo.persisting);
            email.set(l.set(19, null), user);
            person.emailAddresses.add(email, user);
            person.researchGroup = g.find(ResearchGroup.class, n ->{
                    if (n.name != null && n.name.get() != null) {
                        n.name.get().equals(l.set(20, null));
                    }
                    return false;
                }
            );
            boolean doctor = l.set(21, null).equalsIgnoreCase("oui");
            String phdDate = l.set(22, null);

            if (phdDate != null && phdDate != "") {
                person.phdDate.set(parseDate(phdDate), user);
            } else if (doctor) {
                person.phdDate.set(null, user);
            }
            //			System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
            var startDate = l.set(30, null);
            var endDate = l.set(31, null);

            for (var i : List.of(25, 26)) {
                var employer = l.set(i, null);
                person.position = new Position(g, user, InstantiationInfo.persisting); //new Position(graph);
                person.position.employer = g.find(ResearchGroup.class, n ->{
                    if (n.name != null && n.name.get() != null) {
                        n.name.get().equals(employer);
                        }
                    return false;
                    }
                );
                var corps = l.set(i - 2, null);
//                person.position.status = graph.find(Status(g, user), s ->
//                    s.name.get().equals(corps)
//                );

                if (!startDate.isBlank()) {
                    var startDateNode = new DateNode(g, user, InstantiationInfo.persisting);
                    startDateNode.set(parseDate(startDate), user);
                    person.position.from = startDateNode;
                }

                if (!endDate.isBlank()) {
                    var endDateNode = new DateNode(g, user, InstantiationInfo.persisting);
                    endDateNode.set(parseDate(endDate), user);
                    person.position.to = endDateNode;
                }
            }

            person.enposte = l.set(27, null).equals("en poste");
            var quotite = new StringNode(g, user, InstantiationInfo.persisting);
            quotite.set(l.set(29, null), user);
            person.quotite = quotite;
             var researchActivity = new StringNode(g, user, InstantiationInfo.persisting);
            researchActivity.set(l.set(33, null), user);
            person.researchActivity = researchActivity;

//            if (
//                l.stream().anyMatch(Objects::nonNull)
//            ) throw new IllegalStateException("unused columns: " + l);
        }

        System.out.println("Finished loading datalake");
    }
}
