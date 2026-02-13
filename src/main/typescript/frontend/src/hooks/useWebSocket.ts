import { useEffect, useRef, useState } from "react";
import { getWebSocketService } from "@/services/WebSocketService";

/**
 * Hook React pour utiliser WebSocket facilement dans les composants
 */
export const useWebSocket = <T = unknown>(
	eventType: string,
	onMessage?: (data: T) => void,
) => {
	const [isConnected, setIsConnected] = useState(false);
	const wsService = useRef(getWebSocketService());
	const unsubscribeRef = useRef<(() => void) | null>(null);

	useEffect(() => {
		const service = wsService.current;

		// Connexion au WebSocket
		service
			.connect()
			.then(() => {
				setIsConnected(true);
			})
			.catch((error) => {
				console.error("Erreur connexion WebSocket:", error);
				setIsConnected(false);
			});

		// Abonnement aux messages si handler fourni
		if (onMessage) {
			unsubscribeRef.current = service.subscribe(
				eventType,
				onMessage as (data: unknown) => void,
			);
		}

		// Nettoyage à la déconnexion du composant
		return () => {
			if (unsubscribeRef.current) {
				unsubscribeRef.current();
			}
			// Note: On ne déconnecte pas ici car d'autres composants peuvent utiliser le WebSocket
		};
	}, [eventType, onMessage]);

	/**
	 * Envoie un message au serveur
	 */
	const sendMessage = (data: T) => {
		wsService.current.send(eventType, data);
	};

	return {
		sendMessage,
		isConnected,
		wsService: wsService.current,
	};
};
