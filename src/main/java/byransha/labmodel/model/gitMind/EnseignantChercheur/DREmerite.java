package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class DREmerite extends Chercheur {

    public DREmerite(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "DRE2",
                "DRE1",
                "DRECE"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);
    }

    @Override
    public String whatIsThis() {
        return "Directeur de Recherche Émérite";
    }

    @Override
    public String prettyName() {
        return "Directeur de Recherche Émérite";
    }


}
