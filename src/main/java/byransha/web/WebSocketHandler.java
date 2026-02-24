package byransha.web;

import org.java_websocket.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;

/**
 * Gere les messages WebSocket entrants, les traite en fonction de leur type, et orchestre les réponses appropriées.
 */
public class WebSocketHandler {
	
	private final BBGraph graph;
	private final ByranshaWebSocketServer server;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public WebSocketHandler(BBGraph graph, ByranshaWebSocketServer server) {
		this.graph = graph;
		this.server = server;
	}
	
	/**
	 * Traite les messages entrants en fonction de leur type et orchestre les réponses appropriées.
	 */
	public void handleMessage(WebSocket conn, User user, ObjectNode message) {
		String eventType = message.has("type") ? message.get("type").asText() : null;
		
		if (eventType == null) {
			server.sendError(conn, "missing_type", "Message must have a 'type' field");
			return;
		}
		
		System.out.println("WebSocket message from " + user.name.get() + ": " + eventType);
		
		try {
			switch (eventType) {
				case "ping":
					handlePing(conn, user, message);
					break;
					
				case "subscribe":
					handleSubscribe(conn, user, message);
					break;
					
				case "unsubscribe":
					handleUnsubscribe(conn, user, message);
					break;
					
				case "node-update":
					handleNodeUpdate(conn, user, message);
					break;
					
				case "test-echo":
					handleTestEcho(conn, user, message);
					break;
					
				default:
					server.sendError(conn, "unknown_type", "Unknown event type: " + eventType);
			}
		} catch (Exception e) {
			System.err.println("Error handling message type '" + eventType + "': " + e.getMessage());
			e.printStackTrace();
			server.sendError(conn, "processing_error", "Failed to process message: " + e.getMessage());
		}
	}
	
	/**
	 * Traite les messages de type "ping" et repond pour maintenir la connexion active
	 */
	private void handlePing(WebSocket conn, User user, ObjectNode message) {
		ObjectNode pong = mapper.createObjectNode();
		pong.put("type", "pong");
		pong.put("timestamp", System.currentTimeMillis());
		server.sendToConnection(conn, pong);
	}
	
	/**
	 * Gere les demandes d'abonnement (par exemple, s'abonner aux mises à jour des nœuds)
	 */
	private void handleSubscribe(WebSocket conn, User user, ObjectNode message) {
		String channel = message.has("channel") ? message.get("channel").asText() : null;
		
		if (channel == null) {
			server.sendError(conn, "missing_channel", "Subscribe requires 'channel' field");
			return;
		}
		
		// a faire 




		// For now, just acknowledge subscription
		ObjectNode response = mapper.createObjectNode();
		response.put("type", "subscribed");
		response.put("channel", channel);
		server.sendToConnection(conn, response);
		
		System.out.println("User " + user.name.get() + " subscribed to: " + channel);
	}
	
	/**
	 * Gere les demandes de désabonnement
	 */
	private void handleUnsubscribe(WebSocket conn, User user, ObjectNode message) {
		String channel = message.has("channel") ? message.get("channel").asText() : null;
		
		if (channel == null) {
			server.sendError(conn, "missing_channel", "Unsubscribe requires 'channel' field");
			return;
		}
		
		ObjectNode response = mapper.createObjectNode();
		response.put("type", "unsubscribed");
		response.put("channel", channel);
		server.sendToConnection(conn, response);
		
		System.out.println("User " + user.name.get() + " unsubscribed from: " + channel);
	}
	
	/**
	 * Gere les notifications de mise à jour des nœuds (lorsqu'un nœud change)
	 */
	private void handleNodeUpdate(WebSocket conn, User user, ObjectNode message) {
		if (!message.has("nodeId")) {
			server.sendError(conn, "missing_node_id", "Node update requires 'nodeId'");
			return;
		}
		
		int nodeId = message.get("nodeId").asInt();
		BNode node = graph.findByID(nodeId);
		
		if (node == null) {
			server.sendError(conn, "node_not_found", "Node " + nodeId + " not found");
			return;
		}
		
		// Broadcast node update to all other connected clients
		ObjectNode broadcast = mapper.createObjectNode();
		broadcast.put("type", "node-update");
		broadcast.put("nodeId", nodeId);
		broadcast.put("updatedBy", user.name.get());
		broadcast.put("timestamp", System.currentTimeMillis());
		
		// Add node data if requested
		if (message.has("includeData") && message.get("includeData").asBoolean()) {
			broadcast.set("nodeData", node.toJson());
		}
		
		// Broadcast to all except the sender
		server.broadcastExcept(user.id(), broadcast);
		
		System.out.println("📢 Broadcast node update: " + nodeId + " by " + user.name.get());
	}
	
	/**
	 * Gere les messages de test d'écho (pour le débogage)
	 */
	private void handleTestEcho(WebSocket conn, User user, ObjectNode message) {
		ObjectNode echo = mapper.createObjectNode();
		echo.put("type", "test-echo-response");
		echo.put("timestamp", System.currentTimeMillis());
		echo.put("receivedFrom", user.name.get());
		echo.set("originalMessage", message);
		
		server.sendToConnection(conn, echo);
		System.out.println("🔄 Echo test from: " + user.name.get());
	}
	
	/**
	 * Envoie un message de création de nœud à tous les clients connectés
	 */
	public void broadcastNodeCreated(BNode node, User creator) {
		ObjectNode message = mapper.createObjectNode();
		message.put("type", "node-create");
		message.put("nodeId", node.id());
		message.put("nodeClass", node.getClass().getSimpleName());
		message.put("createdBy", creator != null ? creator.name.get() : "system");
		message.put("timestamp", System.currentTimeMillis());
		
		server.broadcastToAll(message);
		System.out.println("Broadcast node created: " + node.id());
	}
	
	/**
	 * Envoie un message de suppression de nœud à tous les clients connectés
	 */
	public void broadcastNodeDeleted(int nodeId, User deletedBy) {
		ObjectNode message = mapper.createObjectNode();
		message.put("type", "node-delete");
		message.put("nodeId", nodeId);
		message.put("deletedBy", deletedBy != null ? deletedBy.name.get() : "system");
		message.put("timestamp", System.currentTimeMillis());
		
		server.broadcastToAll(message);
		System.out.println("Broadcast node deleted: " + nodeId);
	}
	
	/**
	 * Envoie une notification générique à tous les utilisateurs
	 */
	public void broadcastNotification(String title, String message, String level) {
		ObjectNode notification = mapper.createObjectNode();
		notification.put("type", "notification");
		notification.put("title", title);
		notification.put("message", message);
		notification.put("level", level); // info, warning, error, success
		notification.put("timestamp", System.currentTimeMillis());
		
		server.broadcastToAll(notification);
		System.out.println("Broadcast notification: " + title);
	}
	
	/**
	 * Envoie une notification à un utilisateur spécifique
	 */
	public void sendNotificationToUser(int userId, String title, String message, String level) {
		ObjectNode notification = mapper.createObjectNode();
		notification.put("type", "notification");
		notification.put("title", title);
		notification.put("message", message);
		notification.put("level", level);
		notification.put("timestamp", System.currentTimeMillis());
		
		server.sendToUser(userId, notification);
	}
	
	/**
	 * Envoie une notification de résultat de recherche à un utilisateur spécifique (celui qui a effectué la recherche)
	 */
	public void broadcastSearchComplete(User user, int resultCount) {
		ObjectNode message = mapper.createObjectNode();
		message.put("type", "search-result");
		message.put("userId", user.id());
		message.put("username", user.name.get());
		message.put("resultCount", resultCount);
		message.put("timestamp", System.currentTimeMillis());
		
		// Send only to the user who performed the search
		server.sendToUser(user.id(), message);
	}
}
