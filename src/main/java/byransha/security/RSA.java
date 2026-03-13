package byransha.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

	public static KeyPair randomKeyPair() {
		try {
			var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			return keyPairGenerator.generateKeyPair();
		} catch (Throwable err) {
			throw new IllegalStateException(err);
		}
	}

	public static final Cipher cipher;

	static {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException();
		}
	}

	public static byte[] encrypt(byte[] plainData, Key key) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(plainData);
		} catch (Throwable err) {
			return null;
		}
	}

	public static byte[] decrypt(byte[] plainData, Key key) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(plainData);
		} catch (Throwable err) {
			return null;
		}
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		var keys = randomKeyPair();
		var encoded = encrypt("salut".getBytes(), keys.getPublic());
		System.out.println(encoded);
		var decoded = decrypt(encoded, keys.getPrivate());
		System.out.println(new String(decoded));
	}

	public static String toBase64(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	// PEM format (standard, readable)
	public static String toPem(PublicKey key) {
		String base64 = Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(key.getEncoded());
		return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
	}

	// OpenSSH format (matches what id_rsa.pub looks like)
	public static String toOpenSsh(PublicKey key) throws Exception {
		RSAPublicKey rsa = (RSAPublicKey) key;
		java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream out = new java.io.DataOutputStream(buf);

		byte[] alg = "ssh-rsa".getBytes();
		out.writeInt(alg.length);
		out.write(alg);

		byte[] e = rsa.getPublicExponent().toByteArray();
		out.writeInt(e.length);
		out.write(e);

		byte[] n = rsa.getModulus().toByteArray();
		out.writeInt(n.length);
		out.write(n);

		return "ssh-rsa " + Base64.getEncoder().encodeToString(buf.toByteArray());
	}

}
