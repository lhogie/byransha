package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.function.Predicate;

public abstract class FilterNode extends BNode {

    public BooleanNode enabled;

    protected FilterNode(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        enabled = new BooleanNode(g, creator, InstantiationInfo.persisting);
        enabled.set(true, creator); // Activer par défaut (les filtres vides retournent true)
    }

    @Override
    protected void nodeConstructed(User user) {
        super.nodeConstructed(user);
    }

    public abstract boolean filter(BNode node);

    
    //  Vérifie si ce filtre a des valeurs remplies (non vides)
     // Retourne true si le filtre contient des critères de recherche
     
    public abstract boolean hasFilledValues();

    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }
    

    public boolean supportsNodeType(Class<? extends BNode> nodeClass) {
        List<Class<? extends BNode>> supportedTypes = getSupportedTypes();
        if (supportedTypes.isEmpty()) {
            return true;
        }

        return supportedTypes
            .stream()
            .anyMatch(supportedType ->
                supportedType.isAssignableFrom(nodeClass)
            );
    }

    public Predicate<BNode> toPredicate() {
        return node -> {
            if (!enabled.get()) {
                return true;
            }

            if (!supportsNodeType(node.getClass())) {
                return true;
            }

            try {
                return filter(node);
            } catch (Exception e) {
                System.err.println(
                    "Error applying filter " +
                    getClass().getSimpleName() +
                    " to node " +
                    node.id() +
                    ": " +
                    e.getMessage()
                );
                return true;
            }
        };
    }

    public void configure(ObjectNode config, User user) {
        if (config.has("enabled")) {
            enabled.set(config.get("enabled").asBoolean(), user);
        }
    }

    public abstract String getFilterDescription();

    @Override
    public String whatIsThis() {
        return (
            "A filter that can be applied to search results. " +
            getFilterDescription()
        );
    }

    @Override
    public String prettyName() {
        return getFilterDescription();
    }
}
