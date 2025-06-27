package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public EtatCivil etatCivil;

    public Agent(BBGraph g) {
        super(g);

        etatCivil = BNode.create(g, EtatCivil.class);
    }

    public Agent(BBGraph g, int id) {
        super(g, id);
    }

    private String returnName() {
        if( etatCivil.nomUsuel.get() != null && etatCivil.prenom.get() != null) {
            return etatCivil.nomUsuel.get() + " " + etatCivil.prenom.get();
        }
        else if( etatCivil.nomUsuel.get() != null && etatCivil.prenom.get() == null) {
            return etatCivil.nomUsuel.get() + " (pas de prénom)";
        }
        else if( etatCivil.prenom.get() != null && etatCivil.nomUsuel.get() == null) {
            return "(pas de nom) " + etatCivil.prenom.get();
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
