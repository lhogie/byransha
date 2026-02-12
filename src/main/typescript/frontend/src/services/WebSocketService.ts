/**
 * WebSocketService - Service de gestion des connexions WebSocket temps réel
 */

type MessageHandler = (data: unknown) => void;
type EventType = string;

interface WebSocketMessage {
	type: EventType;
	data: unknown;
	timestamp?: number;
}

export class WebSocketService {
	private ws: WebSocket | null = null;
	private url: string;
	private reconnectAttempts = 0;
	private maxReconnectAttempts = 5;
	private reconnectDelay = 3000;
	private listeners: Map<EventType, Set<MessageHandler>> = new Map();
	private isConnecting = false;

	constructor(url: string) {
		this.url = url;
	}

	/**
	 * Connexion au serveur WebSocket
	 */
	connect(): Promise<void> {
		if (this.ws?.readyState === WebSocket.OPEN) {
			return Promise.resolve();
		}

		if (this.isConnecting) {
			return Promise.resolve();
		}

		this.isConnecting = true;

		return new Promise((resolve, reject) => {
			try {
				this.ws = new WebSocket(this.url);

				this.ws.onopen = () => {
					this.reconnectAttempts = 0;
					this.isConnecting = false;
					resolve();
				};

				this.ws.onmessage = (event) => {
					try {
						const message: WebSocketMessage = JSON.parse(event.data);
						this.handleMessage(message);
					} catch (error) {
						console.error("Erreur parsing message WebSocket:", error);
					}
				};

				this.ws.onerror = (error) => {
					console.error("Erreur WebSocket:", error);
					this.isConnecting = false;
					reject(error);
				};

				this.ws.onclose = () => {
					console.log("WebSocket déconnecté");
					this.isConnecting = false;
					this.attemptReconnect();
				};
			} catch (error) {
				this.isConnecting = false;
				reject(error);
			}
		});
	}

	/**
	 * Déconnexion du serveur WebSocket
	 */
	disconnect(): void {
		if (this.ws) {
			this.ws.close();
			this.ws = null;
		}
		this.reconnectAttempts = this.maxReconnectAttempts; // Empêche la reconnexion
	}

	/**
	 * Envoi d'un message au serveur
	 */
	send(type: EventType, data: unknown): void {
		if (this.ws?.readyState === WebSocket.OPEN) {
			const message: WebSocketMessage = {
				type,
				data,
				timestamp: Date.now(),
			};
			this.ws.send(JSON.stringify(message));
		} else {
			console.warn("WebSocket non connecté, message non envoyé:", type);
		}
	}

	/**
	 * Abonnement à un type d'événement
	 */
	subscribe(eventType: EventType, handler: MessageHandler): () => void {
		if (!this.listeners.has(eventType)) {
			this.listeners.set(eventType, new Set());
		}
		this.listeners.get(eventType)?.add(handler);

		// Retourne une fonction de désabonnement
		return () => this.unsubscribe(eventType, handler);
	}

	/**
	 * Désabonnement d'un événement
	 */
	unsubscribe(eventType: EventType, handler: MessageHandler): void {
		this.listeners.get(eventType)?.delete(handler);
	}

	/**
	 * Gestion des messages entrants
	 */
	private handleMessage(message: WebSocketMessage): void {
		const handlers = this.listeners.get(message.type);
		if (handlers) {
			handlers.forEach((handler) => handler(message.data));
		}
	}

	/**
	 * Tentative de reconnexion automatique
	 */
	private attemptReconnect(): void {
		if (this.reconnectAttempts >= this.maxReconnectAttempts) {
			
			return;
		}

		this.reconnectAttempts++;
		

		setTimeout(() => {
			this.connect().catch((error) => {
			});
		}, this.reconnectDelay);
	}

	/**
	 * Vérifie si le WebSocket est connecté
	 */
	isConnected(): boolean {
		return this.ws?.readyState === WebSocket.OPEN;
	}
}

// Instance singleton (optionnel)
let wsServiceInstance: WebSocketService | null = null;

export const getWebSocketService = (url?: string): WebSocketService => {
	if (!wsServiceInstance) {
		const wsUrl =
			url || import.meta.env.PUBLIC_WS_URL || "ws://localhost:8080/ws";
		wsServiceInstance = new WebSocketService(wsUrl);
	}
	return wsServiceInstance;
};
