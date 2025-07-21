package byransha.labmodel.model.v0.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class ChargeDeRecherche extends  Chercheur{

    public ChargeDeRecherche(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "CRCN",
                "CRHC"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);
    }

    @Override
    public String whatIsThis() {
        return "Chargé de Recherche";
    }

    @Override
    public String prettyName() {
        return "Chargé de Recherche";
    }


}
