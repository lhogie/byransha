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
import byransha.network.Peer.PeerListener;
import byransha.system.Byransha;
import byransha.util.ByUtils;

public class Neighborhood extends ServiceNode {

	public PeerListener peerListener = new PeerListener() {

		@Override
		public void peerJoined(Peer p) {
			self.neighbors.add(p);
		}

		@Override
		public void peerLeft(Peer p) {
			self.neighbors.remove(p);
		}
	};

	@ShowInKishanView
	public static final File peersDirectory = new File(Byransha.homeDirectory, "peers");

	@ShowInKishanView
	public final ListNode<Peer> peers = new ListNode<>(this, "peers", Peer.class);

	public final Self self;

	public Neighborhood(NetworkAgent net)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(net);
		peers.elements.add(this.self = new Self(this));

		peersDirectory.mkdirs();

		for (File f : peersDirectory.listFiles()) {
			if (f.isDirectory()) {
				var peer = new OtherPeer(this, f.getName());
				peers.elements.add(peer);
				System.out.println("adding " + peer);
			}
		}

		Queue q = new Queue(this, 6538776544355L);

		ByUtils.loop(() -> 1.0, "Neighborhood message processing", () -> {
			Message msg = q.q.poll_sync();
			var peerNeighborhood = hub().networkAgent.neighborhood;
			var e = (PeerInfo) msg.plainData.content;
			var peer = findPeerByName(e.name);
			peer.lastInfo = e;
			peer.neighbors = e.neighborsName.stream().map(name -> {
				Peer n = peerNeighborhood.findPeerByName(name);

				if (n == null) {
					try {
						n = new OtherPeer(this, name);
						peerNeighborhood.peers.elements.add(n);
					} catch (IOException err) {
						err.printStackTrace();
					}
				}
				return peer;
			}).toList();

			hub().networkAgent.messageOutQueue.considerForwarding(msg, null);
		});
	}

	public void start() {
		ByUtils.loop(() -> 1.2, "discover peers info on disk", () -> {
			for (File peerDirectory : peersDirectory.listFiles()) {
				if (peerDirectory.isDirectory()) {
					var peer = findPeerByName(peerDirectory.getName());

					if (peer == null) {
						System.out
								.println("adding peer " + peerDirectory.getName() + " because it was not in the list");
						try {
							peer = new OtherPeer(this, peerDirectory.getName());
							peers.elements.add(peer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			for (var peer : peers.elements) {
				if (peer instanceof OtherPeer op && !op.directory.exists()) {
					System.out.println("removing peer " + peer.name + " because its directory does not exist anymore");
					peers.elements.remove(peer);
				}
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

}
