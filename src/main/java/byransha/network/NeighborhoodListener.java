package byransha.network;

public interface NeighborhoodListener {
	void joined(Peer p);

	void left(Peer p);
}