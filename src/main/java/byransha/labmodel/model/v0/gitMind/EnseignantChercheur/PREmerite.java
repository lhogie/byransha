package byransha.labmodel.model.v0.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class PREmerite extends EnseignantChercheur{
    public PREmerite(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "PUE1",
                "PUE2",
                "PUECE"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);
    }

    @Override
    public String whatIsThis() {
        return "Professeur Emerite";
    }

    @Override
    public String prettyName() {
        return "Professeur Emerite";
    }
}
