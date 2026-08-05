package byransha.network;

import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import byransha.graph.ShowInKishanView;

public class Self extends Peer {

	@ShowInKishanView
	public PrivateKey privateKey;

	public Self(Neighborhood g) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g, System.getProperty("user.name"));
		KeyPair kp = byransha.security.LocalIdentity.loadOrGenerateRoutingKeys();
		this.publicKey = kp.getPublic();
		this.privateKey = kp.getPrivate();
		this.address = InetAddress.getLocalHost();
	}
}
