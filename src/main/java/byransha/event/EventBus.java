package byransha.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BNode.InstantiationInfo;
import byransha.ListNode;
import byransha.User;

/**
 * Bus d'événements centralisé pour notifier les changements dans le système
 * Thread-safe pour gérer les événements en parallèle
 */
public class EventBus {
    
    private final BBGraph graph;
    private final Map<EventType, List<Consumer<NodeEvent>>> listeners;
    private final ListNode<NodeEvent> globalEventLog;
    
    public EventBus(BBGraph graph) {
        this.graph = graph;
        
        this.listeners = new ConcurrentHashMap<>();
        
        // Créer le log global d'événements (réutilisé si existe déjà dans le graph)
        this.globalEventLog = new ListNode<>(graph, graph.admin(), InstantiationInfo.persisting);
    }
    
    /**
     * Publier un événement - tous les listeners concernés seront notifiés
     */
    public void publish(NodeEvent event) {
        if (event == null) return;
        
        // 1. Persister l'événement
        globalEventLog.add(event, event.user != null ? event.user : graph.admin());
        
        // 2. Logger l'événement
        System.out.println("[EventBus] Event published: " + event.eventType + 
                 " on node #" + (event.targetNode != null ? event.targetNode.id() : "null") + 
                 " by user " + (event.user != null ? event.user.name.get() : "unknown"));
        
        // 3. Notifier les listeners spécifiques
        List<Consumer<NodeEvent>> specificListeners = listeners.get(event.eventType);
        if (specificListeners != null) {
            for (Consumer<NodeEvent> listener : specificListeners) {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    System.err.println("[EventBus] Error in event listener for " + event.eventType + ": " + e.getMessage());
                }
            }
        }
        
        // 4. Notifier les listeners globaux (ALL)
        List<Consumer<NodeEvent>> globalListeners = listeners.get(null);
        if (globalListeners != null) {
            for (Consumer<NodeEvent> listener : globalListeners) {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    System.err.println("[EventBus] Error in global event listener: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * S'abonner à un type d'événement spécifique
     */
    public void subscribe(EventType type, Consumer<NodeEvent> listener) {
        if (listener == null) return;
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    /**
     * S'abonner à TOUS les événements
     * Astuce: utiliser null comme clé pour les listeners globaux
     */
    public void subscribeAll(Consumer<NodeEvent> listener) {
        subscribe(null, listener);
    }
    
    /**
     * Se désabonner d'un type d'événement
     */
    public void unsubscribe(EventType type, Consumer<NodeEvent> listener) {
        if (listener == null) return;
        List<Consumer<NodeEvent>> eventListeners = listeners.get(type);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    
    /**
     * Méthode helper pour créer rapidement un événement simple
     * Pratique pour éviter de créer manuellement l'événement à chaque fois
     */
    public NodeEvent createEvent(EventType type, BNode targetNode, User user, String description) {
        NodeEvent event = new NodeEvent(graph, user != null ? user : graph.admin(), InstantiationInfo.persisting);
        event.eventType = type;
        event.targetNode = targetNode;
        event.user = user;
        if (description != null && !description.isEmpty()) {
            event.description.set(description, user != null ? user : graph.admin());
        }
        return event;
    }
    
    /**
     * Méthode helper pour créer et publier un événement en une seule opération
     */
    public void publishEvent(EventType type, BNode targetNode, User user, String description) {
        NodeEvent event = createEvent(type, targetNode, user, description);
        publish(event);
    }
    
    /**
     * Récupérer tous les événements enregistrés
     */
    public ListNode<NodeEvent> getEventLog() {
        return globalEventLog;
    }
    
    /**
     * Récupérer les événements d'un type spécifique
     */
    public List<NodeEvent> getEventsByType(EventType type) {
        List<NodeEvent> result = new java.util.ArrayList<>();
        for (NodeEvent event : globalEventLog.getElements()) {
            if (event != null && event.eventType == type) {
                result.add(event);
            }
        }
        return result;
    }
}
