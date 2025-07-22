package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class Professeur extends EnseignantChercheur{

    public Professeur(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "PU1",
                "PU2",
                "PUCE"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);
    }

    @Override
    public String whatIsThis() {
        return "Professeur";
    }

    @Override
    public String prettyName() {
        return "Professeur";
    }

}
