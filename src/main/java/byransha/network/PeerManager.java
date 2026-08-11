package byransha.network;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.service.system.Byransha;
import byransha.thread.LoopingThreadNode;

public class PeerManager extends Element {

	List<NeighborhoodListener> neighborhoodListeners = new ArrayList<>();

	@ShowInKishanView
	public static final File peersDirectory = new File(Byransha.homeDirectory, "peers");

	@ShowInKishanView
	public final ListNode<Peer> peers = new ListNode<>(this, null, "peers", Peer.class);

	@ShowInKishanView
	public final Self self;



	public PeerManager(Network net) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		super(net, null);
		peers.elements.add(this.self = new Self(this));
		peersDirectory.mkdirs();

		for (File f : peersDirectory.listFiles()) {
			if (f.isDirectory()) {
				var peer = new OtherPeer(this, f.getName());
				peers.elements.add(peer);
//				System.out.println("adding " + peer);
			}
		}
	}

	public void start() {
		new LoopingThreadNode(this, () -> 1.2, "discover peers info on disk", () -> {
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
		return peers.elements.stream().filter(p -> p != self && p.getConnection() != null).toList();
	}

	public Peer findPeerByName(String name) {
		for (var p : peers.get()) {
			if (p.name != null && p.name.equals(name)) {
				return p;
			}
		}

		return null;
	}

	public Peer findPeer(UUID id) {
		for (var p : peers.get()) {
			if (p.id().equals(id)) {
				return p;
			}
		}

		return null;
	}
}
