package byransha.ai;

import java.net.InetAddress;

import byransha.Service;
import byransha.graph.Hub;
import byransha.graph.list.action.ListNode;
import byransha.network.Message;

public class AiNode extends Service {
	public InetAddress address;
	public int port;
	public String name;
	public double TokensPerSecond;
	public boolean IsComputing;
	public double promptLag;
	public int queueSize;
	public double alpha = 1.0;
	public boolean haveAi = true;

	public AiNode(Hub g) {
		super(g);
	}

	public double getTokensPerSecond() {
		return TokensPerSecond;
	}

	public double getPromptLagMsPerToken() {
		return promptLag;
	}

	public int getCurrentQueueSize() {
		return queueSize;
	}

	public double getAlpha() {
		return alpha;
	}
	public ListNode<Peer> getNeighbors() {
		return hub().network.neighborhood.peers;
	}


	@Override
	public String whatIsThis() {
		return "AI node";
	}

	@Override
	public String toString() {
		return name + (address != null ? " (" + address.getHostName() + ":" + port + ")" : "");
	}

	public double getScore() {
		// calculer Score
		return (TokensPerSecond * alpha) / ((1 + queueSize) * (1 + promptLag));
	}

	public boolean doAnyNeighborsHaveAi() {
		for (Peer peer : getNeighbors().get()) {
			if (peer.haveAi != null && peer.haveAi) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void incomingMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
