package byransha.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class PeerNode extends BNode {
	public InetAddress address;
	public PublicKey publicKey;
	public int port;

	public PeerNode(BGraph g) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g);
		File pubFile = new File(g.byransha.configDirectory.getAbsolutePath(), address.getHostName() + ".pub");

		if (pubFile.exists()) {
			var s = Files.readAllBytes(pubFile.toPath());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(s);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
		} else {
			publicKey = null;
		}
	}

	@Override
	public String whatIsThis() {
		return null;
	}

	@Override
	public String prettyName() {
		return null;
	}

	public int peerID() {
		return publicKey.hashCode();
	}

}
