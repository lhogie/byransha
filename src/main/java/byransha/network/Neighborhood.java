package byransha.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.system.Byransha;
import byransha.util.ByUtils;

public class Neighborhood extends ServiceNode {

	@ShowInKishanView
	public static final File peersDirectory = new File(Byransha.homeDirectory, "peers");

	@ShowInKishanView
	public final ListNode<Peer> peers = new ListNode<>(this, "peers", Peer.class);

	public Neighborhood(NetworkAgent net)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(net);
		peersDirectory.mkdirs();

		for (File f : peersDirectory.listFiles()) {
			if (f.isDirectory()) {
				var peer = new Peer(g(), f.getName());
				peers.elements.add(peer);
				System.out.println("adding " + peer);
			}
		}
	}

	public void start() {
		ByUtils.thread("discover peers info on disk", () -> {
			while (true) {
				for (File peerDirectory : peersDirectory.listFiles()) {
					if (peerDirectory.isDirectory()) {
						var peer = findPeerByName(peerDirectory.getName());

						if (peer == null) {
							System.out.println(
									"adding peer " + peerDirectory.getName() + " because it was not in the list");
							peer = new Peer(g(), peerDirectory.getName());
							peers.elements.add(peer);
						}
					}
				}

				for (var peer : peers.elements) {
					if (!peer.directory.exists()) {
						System.out.println(
								"removing peer " + peer.name + " because its directory does not exist anymore");
						peers.elements.remove(peer);
					}
				}

				sleep(1.2);
			}
		});
	}

	public List<Peer> neighbors() {
		return peers.elements.stream().filter(p -> p.getConnection() != null).toList();
	}

	Peer findPeerByName(String name) {
		for (var p : peers.get()) {
			if (p.name != null && p.name.equals(name)) {
				return p;
			}
		}

		return null;
	}

	public Peer findPeer(int id) {
		for (var p : peers.get()) {
			if (p.id() == id) {
				return p;
			}
		}

		return null;
	}

	@Override
	public void onNewMessage(Message m, Object content) {
		var neighborhood = g().networkAgent.neighborhood;
		var e = (PeerInfo) content;
		var from = findPeerByName(m.routingInfo.source());
		from.lastInfo = e;
		from.neighbors = e.neighborsName.stream().map(name -> {
			Peer peer = neighborhood.findPeerByName(name);

			if (peer == null) {
				peer = new Peer(g(), name);
				peer.name = name;
				neighborhood.peers.elements.add(peer);
			}
			return peer;
		}).toList();

		g().networkAgent.sendQ.considerForwarding(m, null);
	}
}
