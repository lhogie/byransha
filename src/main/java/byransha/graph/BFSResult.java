package byransha.graph;

import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class BFSResult {
	public Object2IntOpenHashMap<Element> distances = new Object2IntOpenHashMap<>();
	public Set<Element> visited = new HashSet<>();

	public int longestDistance() {
		int max = 0;
		for (int d : distances.values()) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}
}