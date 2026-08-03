package byransha.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import byransha.nodes.system.Byransha;

/**
 * Manages the persistent cryptographic identity of the local Byransha node.
 */
public class LocalIdentity {

	private static final String ALGORITHM = "X25519";
	private static final String SECURITY_DIR = "security";
	private static final String PUBLIC_KEY_FILE = "public_key.pem";
	private static final String PRIVATE_KEY_FILE = "private_key.pem";
	private static final File securityDir = new File(Byransha.homeDirectory, SECURITY_DIR);
	private static final File pubFile = new File(securityDir, PUBLIC_KEY_FILE);
	private static final File privFile = new File(securityDir, PRIVATE_KEY_FILE);

	/**
	 * Loads the existing X25519 network routing keys from disk. If they do not
	 * exist, generates a new KeyPair and securely saves them.
	 *
	 * @return A valid KeyPair containing the node's local X25519 keys.
	 */
	public static KeyPair loadOrGenerateRoutingKeys()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		if (pubFile.exists() && privFile.exists()) {
			System.out.println("Loading existing static X25519 keys...");

			PublicKey pubKey = ECC.fromPem(Files.readString(pubFile.toPath()), ALGORITHM);

			PrivateKey privKey = loadPrivateKey(Files.readString(privFile.toPath()));

			return new KeyPair(pubKey, privKey);
		}

		System.out.println("Generating new static X25519 keys for network routing...");
		KeyPair kp = NetworkBox.generateRoutingKeyPair();

		securityDir.mkdirs();

		saveAsPem(pubFile, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----", kp.getPublic().getEncoded());
		saveAsPem(privFile, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----", kp.getPrivate().getEncoded());

		return kp;
	}

	/**
	 * Internal helper to parse a PEM-formatted private key.
	 */
	private static PrivateKey loadPrivateKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String base64 = pem.replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "").replaceAll("\\s", "");
		byte[] decoded = Base64.getDecoder().decode(base64);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		return KeyFactory.getInstance(ALGORITHM).generatePrivate(spec);
	}

	/**
	 * Internal helper to write raw key bytes to disk in standard MIME Base64 PEM
	 * format.
	 */
	private static void saveAsPem(File file, String header, String footer, byte[] keyBytes) throws IOException {
		String base64 = Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(keyBytes);
		String pem = header + "\n" + base64 + "\n" + footer + "\n";
		Files.writeString(file.toPath(), pem);
	}
}
