package byransha.web;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.nodes.system.SystemB;
import byransha.nodes.system.User;

/**
 * Serveur WebSocket pour la communication bidirectionnelle en temps réel.
 * Gère les connexions des clients, l'authentification via des jetons de session (sécurité),
 * et la diffusion de messages aux clients connectés.
 */
public class ByranshaWebSocketServer extends WebSocketServer {
	
	private final BBGraph graph;
	private final SessionStore sessionStore;
	public final WebSocketHandler handler;
	private final ObjectMapper mapper = new ObjectMapper();
	
	// Map: WebSocket connection -> authenticated User
	private final Map<WebSocket, User> authenticatedClients = new ConcurrentHashMap<>();
	
	// Map: User ID -> Set of WebSocket connections (for multi-device support)
	private final Map<Integer, java.util.Set<WebSocket>> userConnections = new ConcurrentHashMap<>();
	
	public ByranshaWebSocketServer(BBGraph graph, SessionStore sessionStore, int port) {
		super(new InetSocketAddress(port));
		this.graph = graph;
		this.sessionStore = sessionStore;
		this.handler = new WebSocketHandler(graph, this);
		
		System.out.println("WebSocket server initializing on port " + port);
	}
	
	@Override
	public void onStart() {
		System.out.println("✓ WebSocket server started successfully on " + getAddress());
		setConnectionLostTimeout(30); // Ping clients every 30s
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("New WebSocket connection from: " + conn.getRemoteSocketAddress());
		
		// Extract session token from query parameters
		String uri = handshake.getResourceDescriptor();
		String sessionToken = extractSessionToken(uri);
		
		if (sessionToken != null) {
			authenticateConnection(conn, sessionToken);
		} else {
			System.out.println("No session token provided, connection unauthenticated");
			// Could send auth required message or close connection
			sendError(conn, "authentication_required", "Session token required");
		}
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		User user = authenticatedClients.remove(conn);
		
		if (user != null) {
			// Remove from user connections map
			java.util.Set<WebSocket> connections = userConnections.get(user.id());
			if (connections != null) {
				connections.remove(conn);
				if (connections.isEmpty()) {
					userConnections.remove(user.id());
				}
			}
			
			System.out.println("User " + user.name.get() + " disconnected (code: " + code + ")");
		} else {
			System.out.println("Unauthenticated connection closed");
		}
	}
	
	@Override
	public void onMessage(WebSocket conn, String message) {
		User user = authenticatedClients.get(conn);
		
		if (user == null) {
			sendError(conn, "unauthenticated", "Please authenticate first");
			return;
		}
		
		try {
			ObjectNode json = (ObjectNode) mapper.readTree(message);
			handler.handleMessage(conn, user, json);
		} catch (Exception e) {
			System.err.println("Error parsing WebSocket message: " + e.getMessage());
			sendError(conn, "invalid_json", "Failed to parse message: " + e.getMessage());
		}
	}
	
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		// Binary messages not currently supported
		sendError(conn, "unsupported", "Binary messages not supported");
	}
	
	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("WebSocket error: " + ex.getMessage());
		if (conn != null) {
			User user = authenticatedClients.get(conn);
			if (user != null) {
				System.err.println("  User: " + user.name.get());
			}
		}
		ex.printStackTrace();
	}
	
	/**
	 * Authentification d'une connexion WebSocket en utilisant un jeton de session.
	 */
	private void authenticateConnection(WebSocket conn, String sessionToken) {
		Optional<SessionStore.SessionData> sessionOpt = sessionStore.getValidSession(sessionToken);
		
		if (sessionOpt.isPresent()) {
			SessionStore.SessionData session = sessionOpt.get();
			User user = (User) graph.findByID(session.userId());
			
			if (user != null) {
				authenticatedClients.put(conn, user);
				
				// Add to user connections for multi-device support
				userConnections.computeIfAbsent(user.id(), k -> ConcurrentHashMap.newKeySet()).add(conn);
				
				System.out.println("User " + user.name.get() + " authenticated via WebSocket");
				
				// Send authentication success
				sendAuthSuccess(conn, user);
			} else {
				System.err.println("User not found for session");
				sendError(conn, "user_not_found", "User not found");
				conn.close(1008, "User not found");
			}
		} else {
			System.out.println("Invalid or expired session token");
			sendError(conn, "invalid_session", "Invalid or expired session");
			conn.close(1008, "Invalid session");
		}
	}
	
	/**
	 * Extract session token from WebSocket URI query parameters
	 * Expected format: ws://host:port/ws?token=SESSION_TOKEN
	 */
	private String extractSessionToken(String uri) {
		if (uri == null || !uri.contains("?")) {
			return null;
		}
		
		String query = uri.substring(uri.indexOf('?') + 1);
		String[] params = query.split("&");
		
		for (String param : params) {
			String[] keyValue = param.split("=", 2);
			if (keyValue.length == 2 && keyValue[0].equals("token")) {
				return keyValue[1];
			}
		}
		
		return null;
	}
	
	/**
	 * Envoie un message de succès d'authentification au client avec les détails de l'utilisateur.
	 */
	private void sendAuthSuccess(WebSocket conn, User user) {
		try {
			ObjectNode response = mapper.createObjectNode();
			response.put("type", "auth_success");
			response.put("userId", user.id());
			response.put("username", user.name.get());
			conn.send(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Envoie un message d'erreur à une connexion spécifique
	 */
	public void sendError(WebSocket conn, String errorCode, String message) {
		try {
			ObjectNode error = mapper.createObjectNode();
			error.put("type", "error");
			error.put("code", errorCode);
			error.put("message", message);
			conn.send(error.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Envoie un message à une connexion spécifique
	 */
	public void sendToConnection(WebSocket conn, ObjectNode message) {
		try {
			conn.send(message.toString());
		} catch (Exception e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}
	
	/**
	 * Partage un message à tous les clients authentifiés
	 */
	public void broadcastToAll(ObjectNode message) {
		String json = message.toString();
		for (WebSocket conn : authenticatedClients.keySet()) {
			try {
				conn.send(json);
			} catch (Exception e) {
				System.err.println("Error broadcasting to client: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Envoie un message à un utilisateur spécifique (tous ses appareils connectés)
	 */
	public void sendToUser(int userId, ObjectNode message) {
		java.util.Set<WebSocket> connections = userConnections.get(userId);
		if (connections != null && !connections.isEmpty()) {
			String json = message.toString();
			for (WebSocket conn : connections) {
				try {
					conn.send(json);
				} catch (Exception e) {
					System.err.println("Error sending to user " + userId + ": " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Envoie un message à tous les utilisateurs sauf un 
	 */
	public void broadcastExcept(int excludeUserId, ObjectNode message) {
		String json = message.toString();
		for (Map.Entry<WebSocket, User> entry : authenticatedClients.entrySet()) {
			if (entry.getValue().id() != excludeUserId) {
				try {
					entry.getKey().send(json);
				} catch (Exception e) {
					System.err.println("Error broadcasting: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Recois le nombre de connexions WebSocket actuellement ouvertes (y compris les utilisateurs non authentifiés)
	 */
	public int getConnectedClientCount() {
		return authenticatedClients.size();
	}
	
	/**
	 * reois le nombre d'utilisateurs uniques actuellement authentifiés
	 */
	public int getAuthenticatedUserCount() {
		return userConnections.size();
	}
	
	/**
	 * Regarde si un utilisateur avec l'ID donné a au moins une connexion WebSocket active
	 */
	public boolean isUserConnected(int userId) {
		return userConnections.containsKey(userId);
	}
}
