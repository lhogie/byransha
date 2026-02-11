package byransha;

import byransha.nodes.BNode;

public class SearchResult implements Comparable<SearchResult> {
	final public String query;
	final public int distance;
	final public BNode rootNode;

	public SearchResult(String q, BNode n, int distance) {
		this.query = q;
		this.distance = distance;
		this.rootNode = n;
	}

	@Override
	public int compareTo(SearchResult o) {
		return Integer.compare(distance, o.distance);
	}

	
}