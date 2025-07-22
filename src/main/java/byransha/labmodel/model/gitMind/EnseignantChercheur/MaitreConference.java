package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.BBGraph;

import java.util.List;

public class MaitreConference extends EnseignantChercheur{


    public MaitreConference(BBGraph g) {
        super(g);
    }

    @Override
    protected void initialized() {
        super.initialized();
        List<String> corpsGradeOptions = List.of(
                "MCFCN",
                "MCFHC"
        );
        corpsGrade.setStaticOptions(corpsGradeOptions);

    }

    @Override
    public String whatIsThis() {
        return "Maitre de Conférence";
    }

    @Override
    public String prettyName() {
        return "Maitre de Conférence";
    }
}
