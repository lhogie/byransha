package byransha.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import byransha.graph.ShowInKishanView;

public class RoutingInfo implements Serializable {
	public List<String> actualRoute = new ArrayList<>();
	public List<String> suggestedRoute = new ArrayList<>();
	public String nameOfRecipient;

	@Override
	public String toString() {
		return "route: " + actualRoute;
	}

	@ShowInKishanView
	public List<String> route() {
		return actualRoute;
	}

	@ShowInKishanView
	public String nameOfSender() {
		return actualRoute.getFirst();
	}

	@ShowInKishanView
	public String nameOfRecipient() {
		return nameOfRecipient;
	}
}