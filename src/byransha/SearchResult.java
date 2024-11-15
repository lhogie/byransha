package byransha;

public class SearchResult implements Comparable<SearchResult> {
	final public String query;
	final public int distance;
	final public GOBMNode rootNode;

	public SearchResult(String q, GOBMNode n, int distance) {
		this.query = q;
		this.distance = distance;
		this.rootNode = n;
	}

	@Override
	public int compareTo(SearchResult o) {
		return Integer.compare(distance, o.distance);
	}

	
}