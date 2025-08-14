package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public EtatCivil etatCivil;

    public Agent(BBGraph g, User creator) {
        super(g, creator);

        etatCivil = new EtatCivil(g, creator);
        endOfConstructor();
    }

    public Agent(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    private String returnName() {
        if(etatCivil == null) {
            return null;
        }
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
