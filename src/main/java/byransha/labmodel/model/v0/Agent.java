package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public Out<EtatCivil> etatCivil;

    public Agent(BBGraph g, User creator) {
        super(g, creator);

        etatCivil = new Out<>(g, creator);
        endOfConstructor();
    }

    public Agent(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    @Override
    public String toString() {
        return prettyName();
    }

    @Override
    public String prettyName() {
        if(etatCivil == null) {
            return null;
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
