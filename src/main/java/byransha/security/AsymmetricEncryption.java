package byransha.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import byransha.graph.Root;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.Byransha;

public class AsymmetricEncryption extends ServiceNode {
	@ShowInKishanView
	File securityDir = new File(Byransha.homeDirectory, "security");

	@ShowInKishanView
	public PublicKey publicKey;
	public PrivateKey privateKey;

	@ShowInKishanView
	final StringNode publicKeyDisplay = new StringNode(this);

	public AsymmetricEncryption(Root net) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		super(net);
		File publicKeyFile = new File(securityDir, "public_key.pem");
		File privateKeyFile = new File(securityDir, "private_key.pem");

		if (publicKeyFile.exists() && privateKeyFile.exists()) {
			this.publicKey = (PublicKey) RSA.fromPem(Files.readString(publicKeyFile.toPath()));
			this.privateKey = (PrivateKey) RSA.fromPem(Files.readString(privateKeyFile.toPath()));
		} else {
			System.out.println("Generating new random RSA keys");
			var keyPair = RSA.randomKeyPair();
			this.publicKey = keyPair.getPublic();
			this.privateKey = keyPair.getPrivate();
			publicKeyFile.getParentFile().mkdirs();
			Files.writeString(publicKeyFile.toPath(), RSA.toPem(publicKey));
			Files.writeString(privateKeyFile.toPath(), RSA.toPem(privateKey));
			System.out.println("public key: " + publicKeyAsString());
			publicKeyDisplay.set(publicKeyAsString());
		}
	}

	public String publicKeyAsString() {
		return new String(RSA.toBase64(publicKey));
	}

	@Override
	public String toString() {
		return "";
	}
}
