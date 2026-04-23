package byransha.network;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.security.PublicKey;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class PeerNode extends BNode {
	public InetAddress address;
	public PublicKey publicKey;
	public int port;
	public String name;
	public DataOutputStream out;

	public PeerNode(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return null;
	}

	@Override
	public String toString() {
		return address.getHostName() + ":" + port + "/" + peerID();
	}

	public int peerID() {
		return publicKey.hashCode();
	}

}
