package byransha.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import byransha.graph.ActionMethod;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.network.Neighborhood;
import byransha.network.NetworkAgent;
import byransha.primitive.StringNode;

public class PublicKeyImporter extends ServiceNode {
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
		var f = new File(Neighborhood.peersDirectory, peerName.get() + "/publicKey.pem");
		f.getParentFile().mkdirs();
		Files.writeString(f.toPath(), publicKey.get());
	}
}
