package byransha.network;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class PrivateKeyUtils {
	public static PrivateKey readPrivateKey(Path path) throws Exception {
		String pem = Files.readString(path);

		String base64 = pem.replaceAll("-----BEGIN [A-Z ]+-----", "").replaceAll("-----END [A-Z ]+-----", "")
				.replaceAll("\\s", "");

		byte[] der = Base64.getDecoder().decode(base64);

		// Try PKCS#8 first (BEGIN PRIVATE KEY)
		try {
			return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
		} catch (InvalidKeySpecException e) {
			// Fall back to PKCS#1 (BEGIN RSA PRIVATE KEY) via BouncyCastle
			return readPkcs1PrivateKey(path);
		}
	}

	// BouncyCastle fallback for PKCS#1 format
	private static PrivateKey readPkcs1PrivateKey(Path path) throws Exception {
		try (PEMParser parser = new PEMParser(Files.newBufferedReader(path))) {
			Object obj = parser.readObject();
			if (obj instanceof PEMKeyPair pemKeyPair) {
				return new JcaPEMKeyConverter().setProvider("BC").getKeyPair(pemKeyPair).getPrivate();
			}
			if (obj instanceof PrivateKeyInfo info) {
				return new JcaPEMKeyConverter().setProvider("BC").getPrivateKey(info);
			}
			throw new IllegalArgumentException("Unrecognized key format: " + obj.getClass());
		}
	}

}
