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
				try {
					var peer = new Peer(g());
					peer.setDirectory(f);
					peers.elements.add(peer);
					System.out.println("adding " + peer);
				} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
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
							try {
								peer = new Peer(g());
								peer.setDirectory(peerDirectory);
								peers.elements.add(peer);
							} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				sleep(1.2);
			}
		});
	}

	public List<Peer> neighbors() {
		return peers.elements.stream().filter(p -> p.getConnection() != null).toList();
	}

	public Peer findPeerByName(String name) {
		for (var p : peers.get()) {
			if (p.name != null && p.name.equals(name)) {
				return p;
			}
		}

		return null;
	}

	public Peer findPeer(int id) {
		for (var p : peers.get()) {
			if (p.id == id) {
				return p;
			}
		}

		return null;
	}

}
