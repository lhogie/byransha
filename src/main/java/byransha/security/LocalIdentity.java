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
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import byransha.service.system.Byransha;

/**
 * Manages the persistent cryptographic identity of the local Byransha node.
 */
public class LocalIdentity {

	public static final String ALGORITHM = "X25519";
	public static final String publicKeyFileName = "public_key.pem";
	public static final String privateKeyFileName = "private_key.pem";
	public static final File localPeerDirectory = Byransha.homeDirectory;
	public static final File pubFile = new File(Byransha.homeDirectory, publicKeyFileName);
	public static final File privFile = new File(Byransha.homeDirectory, privateKeyFileName);

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

			PublicKey pubKey = loadPublicKey(Files.readString(pubFile.toPath()));

			PrivateKey privKey = loadPrivateKey(Files.readString(privFile.toPath()));

			return new KeyPair(pubKey, privKey);
		}

		System.out.println("Generating new static X25519 keys for network routing...");
		KeyPair kp = NetworkBox.generateRoutingKeyPair();

		localPeerDirectory.mkdirs();

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
	 * Internal helper to parse a PEM-formatted public key.
	 */
	private static PublicKey loadPublicKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String base64 = pem.replaceAll("-----(BEGIN|END) PUBLIC KEY-----", "").replaceAll("\\s", "");
		byte[] decoded = Base64.getDecoder().decode(base64);

		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		return KeyFactory.getInstance(ALGORITHM).generatePublic(spec);
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

	/**
	 * Parses a PEM-formatted public key string into a PublicKey object.
	 *
	 * @param publicKeyString
	 *                            The PEM string containing the public key.
	 * @param algorithm
	 *                            The algorithm of the key (e.g., "X25519").
	 * @return A valid PublicKey object.
	 */
	public static PublicKey fromPem(String publicKeyString, String algorithm)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Strip out the PEM headers/footers and any whitespace
		String base64 = publicKeyString
				.replaceAll("-----(BEGIN|END) PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		// Decode the Base64 payload
		byte[] decoded = Base64.getDecoder().decode(base64);

		// Generate the PublicKey using the specified algorithm
		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		return KeyFactory.getInstance(algorithm).generatePublic(spec);
	}
}
