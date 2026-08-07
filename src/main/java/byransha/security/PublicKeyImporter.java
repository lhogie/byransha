package byransha.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import byransha.graph.ActionMethod;
import byransha.graph.ShowInKishanView;
import byransha.network.NetworkAgent;
import byransha.network.PeerManager;
import byransha.primitive.StringNode;
import byransha.system.SystemNode;

public class PublicKeyImporter extends SystemNode {
	@ShowInKishanView
	public final StringNode peerName = new StringNode(this);
	@ShowInKishanView
	public final StringNode publicKey = new StringNode(this, "", ".+");

	public PublicKeyImporter(NetworkAgent g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a import a public key";
	}

	@ActionMethod
	public void importKey() throws IOException {
		var f = new File(PeerManager.peersDirectory, peerName.get() + "/publicKey.pem");
		f.getParentFile().mkdirs();
		Files.writeString(f.toPath(), publicKey.get());
	}
}
