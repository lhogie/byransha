package butils;


import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncoder {
	private Cipher encryptCipher;
	public final KeyPair keyPair;

	public RSAEncoder() {
		this(randomKeyPair());
	}
	
	public RSAEncoder(KeyPair kp) {
		try {
			this.keyPair = kp;
			this.encryptCipher = Cipher.getInstance("RSA");
			encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
		} catch (Throwable err) {
			throw new IllegalStateException(err);
		}
	}

	public byte[] encode(byte[] plainData) {
		try {
			return encryptCipher.doFinal(plainData);
		} catch (Throwable err) {
			throw new IllegalStateException(err);
		}
	}

	static Map<PublicKey, Cipher> m = new HashMap<>();

	public static KeyPair randomKeyPair() {
		try {
			var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			return keyPairGenerator.generateKeyPair();
		} catch (Throwable err) {
			throw new IllegalStateException(err);
		}
	}

	public static byte[] decode(PublicKey publicKey, byte[] encodedDataBytes) {
		var decryptCipher = m.get(publicKey);

		try {
			if (decryptCipher == null) {
				m.put(publicKey, decryptCipher = Cipher.getInstance("RSA"));
				decryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
			}

			return decryptCipher.doFinal(encodedDataBytes);
		} catch (Throwable err) {
			throw new IllegalStateException(err);
		}
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		var g = new RSAEncoder();
//		System.out.println(g.keyPair.getPublic().get);
		var encoded = g.encode("salut".getBytes());
		System.out.println(encoded);
		var decoded = g.decode(g.keyPair.getPublic(), encoded);
		System.out.println(new String(decoded));
	}

}
