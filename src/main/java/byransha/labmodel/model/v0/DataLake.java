package byransha.labmodel.model.v0;

import byransha.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import toools.io.file.RegularFile;

public class DataLake extends BNode {

    public File inputDir;

    public DataLake(BBGraph g) {
        super(g);
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
        if(inputDir == null) {return;}
        else if(!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IOException("Input directory does not exist or not a directory: " + inputDir);
        }

        Files.readAllLines(
            new File(inputDir, "CH_Nationality_List_20171130_v1.csv").toPath()
        ).forEach(l -> {
                var c = graph.create(Nationality.class);
                c.set(l);
            });

        Lab i3s = graph.create(Lab.class);

        for (var n : List.of("CNRS", "Inria")) {
            var epst = graph.create(EPST.class); //new EPST(graph);
            epst.name.set(n);
            i3s.tutelles.add(epst);
        }

        var UniCA = graph.create(University.class); //new University(graph);
        UniCA.name.set("UniCA");
        i3s.tutelles.add(UniCA);

        for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
            var group = graph.create(ResearchGroup.class); //new ResearchGroup(graph);
            group.name.set(n);
            i3s.subStructures.add(group);
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
            var campus = graph.create(Campus.class); //new Campus(graph);
            campus.name.set(n);
            UniCA.campuses.add(campus);
        }

        var upToDateDir = new File(inputDir, "marijo_tableau_de_bord");
        var oldDir = new File(upToDateDir, "old");

        var csv = new CSV(new File(oldDir, "TB_personneI3S_IT.csv"), ";");

        for (var l : csv) {
            var person = graph.create(Person.class); //new Person(graph);

            if (l.set(0, null).equals("member")) {
                var position = graph.create(Position.class);
                position.employer = i3s;
                person.positions.add(position);
            }

            			person.etatCivil.name.set(l.set(1, null));
            			person.etatCivil.familyNameBeforeMariage.set(l.set(2, null));
            			person.etatCivil.firstName.set(l.set(3, null));
            			person.etatCivil.birthDate.set(l.set(4, null));
            			person.etatCivil.cityOfBirth.set(l.set(5, null));
//            person.etatCivil.countryOfBirth.set(l.set(6, null));
//            person.etatCivil.nationality.set(l.set(7, null));
            			person.etatCivil.address.set(l.set(8, null));
            var inter = graph.create(StringNode.class);
            inter.set(l.set(9, null));
            person.phoneNumbers.add(inter);

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
                                person.offices.add((Office) office);
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
                var n = graph.create(StringNode.class); //new StringNode(this, phoneNumber);
                n.set(phoneNumber);
                person.phoneNumbers.add(n);
            }

            person.badgeNumber.set(l.set(16, null));
            person.website.set(l.set(17, null));
            person.faxNumber.set(l.set(18, null));
            var email = graph.create(EmailNode.class);
            email.set(l.set(19, null));
            person.emailAddresses.add(email);
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
                person.phdDate.set(phdDate);
            } else if (doctor) {
                person.phdDate.set("unknown");
            }
            //			System.err.println(l.stream().map(e-> e == null ? "" : "-").toList());
            var startDate = l.set(30, null);
            var endDate = l.set(31, null);

            for (var i : List.of(25, 26)) {
                var employer = l.set(i, null);
                person.position = graph.create(Position.class); //new Position(graph);
                person.position.employer = graph.find(ResearchGroup.class, n ->{
                    if (n.name != null && n.name.get() != null) {
                        n.name.get().equals(employer);
                        }
                    return false;
                    }
                );
                var corps = l.set(i - 2, null);
//                person.position.status = graph.find(Status.class, s ->
//                    s.name.get().equals(corps)
//                );

                if (!startDate.isBlank()) {
                    var startDateNode = graph.create(DateNode.class);
                    startDateNode.set(startDate);
                    person.position.from = startDateNode;
                }

                if (!endDate.isBlank()) {
                    var endDateNode = graph.create(DateNode.class);
                    endDateNode.set(endDate);
                    person.position.to = endDateNode;
                }
            }

            person.enposte = l.set(27, null).equals("en poste");
            person.position.comment = graph.create(StringNode.class);
            person.position.comment.set(  l.set(28, null));
            var quotite = graph.create(StringNode.class);
            quotite.set(l.set(29, null));
            person.quotite = quotite; //new StringNode(this, l.set(29, null));
            person.position.comment.set(person.position.comment.get() + "\n"+ l.set(32, null));
            var researchActivity = graph.create(StringNode.class);
            researchActivity.set(l.set(33, null));
            person.researchActivity = researchActivity; //new StringNode(this, l.set(33, null));

//            if (
//                l.stream().anyMatch(Objects::nonNull)
//            ) throw new IllegalStateException("unused columns: " + l);
        }
    }
}
