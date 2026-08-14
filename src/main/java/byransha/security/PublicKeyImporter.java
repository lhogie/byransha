package byransha.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import byransha.Service;
import byransha.action.ActionMethod;
import byransha.action.base.ShowInKishanView;
import byransha.network.Message;
import byransha.network.Network;
import byransha.network.PeerManager;
import byransha.primitive.StringNode;

public class PublicKeyImporter extends Service {
	@ShowInKishanView
	public final StringNode peerName = new StringNode(this, null, "", ".+");
	@ShowInKishanView
	public final StringNode publicKey = new StringNode(this, null, "", ".+");

	public PublicKeyImporter(Network g) {
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

	@Override
	protected void incomingMessage(Message msg) {
		// TODO Auto-generated method stub

	}
}
