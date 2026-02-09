package byransha.event;

/**
 * Types d'événements système pour l'EventBus
 */
public enum EventType {
    NODE_CREATED,
    NODE_UPDATED,
    NODE_DELETED,
    VALUE_CHANGED,
    SEARCH_EXECUTED,
    FILTER_APPLIED,
    FILTER_CREATED,
    USER_LOGIN,
    USER_LOGOUT,
    ENDPOINT_EXECUTED,
    SLOW_QUERY,
    ERROR_OCCURRED,
}
