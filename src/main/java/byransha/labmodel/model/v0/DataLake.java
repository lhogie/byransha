package byransha.labmodel.model.v0;

import byransha.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.List;

public class DataLake extends BNode {

    public final File inputDir;

    public DataLake(BBGraph g, User creator, File dir) {
        super(g, creator);
    }

    @Override
    public String whatIsThis() {
        return "a data lake";
    }

    @Override
    public String prettyName() {
        return "I3S datalake";
    }

    public void load() throws IOException {

        User user = new User(graph, null);

        if(inputDir == null) {return;}
        else if(!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IOException("Input directory does not exist or not a directory: " + inputDir);
        }

        Files.readAllLines(
            new File(inputDir, "CH_Nationality_List_20171130_v1.csv").toPath()
        ).forEach(l -> {
                var c = new Nationality(graph, user);
                c.set(l, user);
            });

        Lab i3s = new Lab(graph, user);

        for (var n : List.of("CNRS", "Inria")) {
            var epst = new EPST(graph, user); //new EPST(graph);
            epst.name.set(n, user);
            i3s.tutelles.add(epst, user);
        }

        var UniCA = new University(graph, user); //new University(graph);
        UniCA.name.set("UniCA", user);
        i3s.tutelles.add(UniCA, user);

        for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
            var group = new ResearchGroup(graph, user); //new ResearchGroup(graph);
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
            var campus = new Campus(graph, user); //new Campus(graph);
            campus.name.set(n, user);
            UniCA.campuses.add(campus, user);
        }

        var upToDateDir = new File(inputDir, "marijo_tableau_de_bord");
        var oldDir = new File(upToDateDir, "old");

        var csv = new CSV(new File(oldDir, "TB_personneI3S_IT.csv"), ";");

        for (var l : csv) {
            var person = new Person(graph, user); //new Person(graph);

            if (l.set(0, null).equals("member")) {
                var position = new Position(graph, user);
                position.employer = i3s;
                person.positions.add(position, user);
            }

            var name = new StringNode(graph, user, l.set(1, null));
            var time = OffsetDateTime.parse(l.set(4, null));
            person.etatCivil.name.set(name, user);
            person.etatCivil.familyNameBeforeMariage.get().set(l.set(2, null), user);
            person.etatCivil.firstName.get().set(l.set(3, null), user);
            person.etatCivil.birthDate.get().set(time, user);
            person.etatCivil.cityOfBirth.get().set(l.set(5, null), user);
//            person.etatCivil.countryOfBirth.set(l.set(6, null));
//            person.etatCivil.nationality.set(l.set(7, null));
            person.etatCivil.address.get().set(l.set(8, null), user);
            var inter = new StringNode(graph, user);
            inter.set(l.set(9, null), user);
            person.phoneNumbers.add(inter, user);

            var officeName = l.set(15, null);

            for (var campusName : List.of(l.set(10, null), l.set(11, null))) {
                if (!campusName.isBlank()) {
                    var campus = graph.find(Campus.class, n -> {
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
                var n = new StringNode(graph, user); //new StringNode(this, phoneNumber);
                n.set(phoneNumber, user);
                person.phoneNumbers.add(n, user);
            }

            person.badgeNumber.set(l.set(16, null), user);
            person.website.set(l.set(17, null), user);
            person.faxNumber.set(l.set(18, null), user);
            var email = new EmailNode(graph, user);
            email.set(l.set(19, null), user);
            person.emailAddresses.add(email, user);
            person.researchGroup = graph.find(ResearchGroup.class, n ->{
                    if (n.name != null && n.name.get() != null) {
                        n.name.get().equals(l.set(20, null));
                    }
                    return false;
                }
            );
            boolean doctor = l.set(21, null).equalsIgnoreCase("oui");
            String phdDate = l.set(22, null);

            if (phdDate != null) {
                person.phdDate.set(OffsetDateTime.parse(phdDate), user);
            } else if (doctor) {
                person.phdDate.set(null, user);
            }
            //			System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
            var startDate = l.set(30, null);
            var endDate = l.set(31, null);

            for (var i : List.of(25, 26)) {
                var employer = l.set(i, null);
                person.position = new Position(graph, user); //new Position(graph);
                person.position.employer = graph.find(ResearchGroup.class, n ->{
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
                    var startDateNode = new DateNode(graph, user);
                    startDateNode.set(OffsetDateTime.parse(startDate), user);
                    person.position.from = startDateNode;
                }

                if (!endDate.isBlank()) {
                    var endDateNode = new DateNode(graph, user);
                    endDateNode.set(OffsetDateTime.parse(endDate), user);
                    person.position.to = endDateNode;
                }
            }

            person.enposte = l.set(27, null).equals("en poste");
            person.position.comment = new StringNode(graph, user);
            person.position.comment.set(  l.set(28, null), user);
            var quotite = new StringNode(graph, user);
            quotite.set(l.set(29, null), user);
            person.quotite = quotite; //new StringNode(this, l.set(29, null));
            person.position.comment.set(person.position.comment.get() + "\n"+ l.set(32, null), user);
            var researchActivity = new StringNode(graph, user);
            researchActivity.set(l.set(33, null), user);
            person.researchActivity = researchActivity; //new StringNode(this, l.set(33, null));

//            if (
//                l.stream().anyMatch(Objects::nonNull)
//            ) throw new IllegalStateException("unused columns: " + l);
        }
    }
}
