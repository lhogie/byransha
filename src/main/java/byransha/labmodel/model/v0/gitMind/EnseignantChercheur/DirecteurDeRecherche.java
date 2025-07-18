package byransha.labmodel.model.v0.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class DirecteurDeRecherche extends Chercheur{

    public DirecteurDeRecherche(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "DR2",
                "DR1",
                "DRCE"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);
    }

    @Override
    public String whatIsThis() {
        return "Directeur de Recherche";
    }

    @Override
    public String prettyName() {
        return "Directeur de Recherche";
    }


}
