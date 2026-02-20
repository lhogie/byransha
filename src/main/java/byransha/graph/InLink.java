package byransha.graph;

public record InLink(String role, BNode source) {
	@Override
	public String toString() {
		return source + "." + role;
	}
}