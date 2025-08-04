package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public EtatCivil etatCivil;

    public Agent(BBGraph g) {
        super(g);

        etatCivil = g.create(EtatCivil.class);
    }

    public Agent(BBGraph g, int id) {
        super(g, id);
    }

    private String returnName() {
        if( etatCivil.name.get() != null && etatCivil.firstName.get() != null) {
            return etatCivil.name.get() + " " + etatCivil.firstName.get();
        }
        else if( etatCivil.name.get() != null && etatCivil.firstName.get() == null) {
            return etatCivil.name.get() + " (pas de prénom)";
        }
        else if( etatCivil.firstName.get() != null && etatCivil.name.get() == null) {
            return "(pas de nom) " + etatCivil.firstName.get();
        }
        return "Agent with no civil information ";
    }

    @Override
    public String toString() {
        return returnName();
    }

    @Override
    public String prettyName() {
        return returnName();
    }

    @Override
    public String whatIsThis() {
        return returnName();
    }
}
