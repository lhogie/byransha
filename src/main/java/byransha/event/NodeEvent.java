package byransha.event;

import java.util.Date;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BNode.InstantiationInfo;
import byransha.MapNode;
import byransha.StringNode;
import byransha.User;

/**
 * Événement lié à un node dans le système
 * Étend BNode pour être persistable dans le graph
 */
public class NodeEvent extends BNode {
    public EventType eventType;
    public Date timestamp;
    public BNode targetNode;
    public User user;
    public StringNode description;
    public MapNode metadata;
    
    public NodeEvent(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);

        timestamp = new Date();
        endOfConstructor();
    }
    
    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        
        targetNode = null;  // À définir lors de l'utilisation
        user = creator;      // L'utilisateur qui a créé l'événement
        description = new StringNode(g, creator, InstantiationInfo.persisting);
        metadata = new MapNode(g, creator, InstantiationInfo.persisting);
    }
    
    @Override
    public String whatIsThis() {
        return "Un événement système";
    }
    
    @Override
    public String prettyName() {
        return eventType + " on node #" + (targetNode != null ? targetNode.id() : "null") + " by " + (user != null ? user : "unknown");
    }
}
