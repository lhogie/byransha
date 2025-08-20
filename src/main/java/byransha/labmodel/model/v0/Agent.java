package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    private Out<EtatCivil> etatCivil;

    public Agent(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        etatCivil = new Out<>(g, creator);
        var newEtatCivil = new EtatCivil(g, creator, InstantiationInfo.persisting);
        etatCivil.set(newEtatCivil, creator);
    }

    @Override
    public String prettyName() {
        if(etatCivil == null || etatCivil.get() == null) {
            return "Agent with no civil information";
        }
        if( etatCivil.get().name.get() != null && etatCivil.get().firstName.get() != null) {
            return etatCivil.get().name.get() + " " + etatCivil.get().firstName.get();
        }
        else if( etatCivil.get().name.get() != null && etatCivil.get().firstName.get() == null) {
            return etatCivil.get().name.get() + " (pas de prénom)";
        }
        else if( etatCivil.get().firstName.get() != null && etatCivil.get().name.get() == null) {
            return "(pas de nom) " + etatCivil.get().firstName.get();
        }
        return "Agent with no civil information ";
    }

    @Override
    public String whatIsThis() {
        return "an agent";
    }
}
