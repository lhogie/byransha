package byransha.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import byransha.graph.ShowInKishanView;
import byransha.security.ECC;

public class OtherPeer extends Peer {
	@ShowInKishanView
	public final File directory;

	public OtherPeer(Neighborhood g, String name) throws IOException
			 {
		super(g, name);
		this.directory = new File(Neighborhood.peersDirectory, name);

		{
			var publicKeyFile = new File(directory, "public_key.pem");

			if (publicKeyFile.exists()) {
				var publicKeyString = Files.readString(publicKeyFile.toPath());
				this.publicKey = ECC.fromPem(publicKeyString, "X25519");
			} else {
				System.err.println("no public key for " + this);
			}

		}

		this.autoConnect.set(!new File(directory, "noAutoConnect").exists());
		this.autoConnect.valueChangeListeners.add((n, old, autoConnect) -> {
			if (autoConnect) {
				new File(directory, "noAutoConnect").delete();
			} else {
				try {
					new File(directory, "noAutoConnect").createNewFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		{
			var ipFile = new File(directory, "ip.txt");

			if (ipFile.exists()) {
				var ipS = Files.readString(ipFile.toPath()).trim();
				this.address = s2ip(ipS);
			} else {
				System.err.println("no IP known for " + this);
			}
		}
	}
}
