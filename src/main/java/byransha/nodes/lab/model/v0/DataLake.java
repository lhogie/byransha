package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataLake extends BNode {

    public final File inputDir;

    public DataLake(BBGraph g, User creator, File dir) {
        super(g, creator);
        inputDir = dir;
    }

    public static OffsetDateTime parseDate(String date) {
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

    @Override
    public String whatIsThis() {
        return "a data lake";
    }

    @Override
    public String prettyName() {
        return "datalake at " + inputDir.getAbsolutePath();
    }

    public void load() throws IOException {
        if (inputDir == null)
            throw new NullPointerException();

        if (!inputDir.exists() || !inputDir.isDirectory())
            throw new IOException("Input directory does not exist or not a directory: " + inputDir);

        System.out.println("Loading datalake from " + inputDir);
        User user = g.systemUser();


        Files.readAllLines(
                new File(inputDir, "CH_Nationality_List_20171130_v1.csv").toPath()
        ).forEach(l -> {
            var c = new Nationality(g, user);
            c.set(l, user);
        });

        Lab i3s = new Lab(g, user);

        for (var n : List.of("CNRS", "Inria")) {
            var epst = new EPST(g, user);
            epst.name.set(n, user);
            i3s.tutelles.add(epst, user);
        }

        var UniCA = new University(g, user); //new University(graph);
        UniCA.name.set("UniCA", user);
        i3s.tutelles.add(UniCA, user);

        for (var n : List.of("COMRED", "SIS", "MDSC", "SPARKS")) {
            var group = new ResearchGroup(g, user); //new ResearchGroup(graph);
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
            var campus = new Campus(g, user); //new Campus(graph);
            campus.name.set(n, user);
            UniCA.campuses.add(campus, user);
        }

        OldTBRH.loadOLDTBRH(i3s, user, inputDir);


        System.out.println(" Finished loading datalake");
    }


}
