package byransha.web.endpoint;

import byransha.BBGraph;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.nodes.BNode;
import byransha.SearchForm; 
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Jump extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "navigating to a target node.";
	}

	public Jump(BBGraph g) {
		super(g);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		// if(node != user.currentNode()){
	
		// Ne pas ajouter à l'historique si :
		// 1. C'est le nœud courant (évite les doublons)
		// 2. C'est un résultat de recherche (dans la liste results d'un SearchForm)
		boolean shouldAddToHistory = node != user.currentNode() 
			&& !isSearchResult(node, user);
		
		if(shouldAddToHistory){
			
			user.stack.add(node);
		}

		NodeInfo nodeInfoEndpoint = g.findEndpoint(NodeInfo.class);
		if (nodeInfoEndpoint == null) {
			return ErrorResponse.serverError("NodeInfo endpoint not found in the graph.");
		}
		in.removeAll();

		return nodeInfoEndpoint.exec(in, user, webServer, exchange, node);
	}
	
	/**
	 * Vérifie si le nœud est un résultat provenant d'un SearchForm
	 */
	private boolean isSearchResult(BNode node, User user) {
		// Parcourt l'historique récent pour voir si on vient d'un SearchForm
		if (user.stack.isEmpty()) return false;
		
		BNode previousNode = user.currentNode();
		if (previousNode instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) previousNode;
			// Vérifie si le nœud fait partie des résultats de recherche
			return searchForm.results.getElements().contains(node);
		}
		
		return false;
	}
}
