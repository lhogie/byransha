package byransha.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
	public List<String> route = new ArrayList<>();
	public long targetNodeId;
	public Object content;

	@Override
	public String toString() {
		return "route: " + route + ", content: " + content;
	}
}