package byransha.security;

import software.pando.crypto.nacl.CryptoBox;
import software.pando.crypto.nacl.SecretBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Facade for Byransha network routing cryptography.
 * 
 * Utilizes the NaCl standard (X25519 + XSalsa20-Poly1305) to ensure secure, 
 * authenticated End-to-End (E2E) and Hop-by-Hop communication.
 */
public class NetworkBox {

	/**
	 * Generates a new X25519 KeyPair specifically for network routing.
	 * 
	 * @return A new KeyPair for X25519 agreement.
	 */
	public static KeyPair generateRoutingKeyPair() {
		return CryptoBox.keyPair();
	}

	/**
	 * Encrypts and authenticates a payload using X25519 key agreement
	 * followed by XSalsa20-Poly1305.
	 * 
	 * @param senderPriv  The private key of the sender (e.g., Node A).
	 * @param receiverPub The public key of the recipient (e.g., Node D).
	 * @param payload     The raw data to encrypt (e.g.,
	 *                    CBOR-serialized object).
	 * @return The encrypted payload, including the nonce and MAC tag.
	 */
	public static byte[] encrypt(PrivateKey senderPriv, PublicKey receiverPub, byte[] payload) {
		try {
			CryptoBox box = CryptoBox.encrypt(senderPriv, receiverPub, payload);
			return toByteArray(box);
		} catch (Exception e) {
			throw new SecurityException("NetworkBox asymmetric encryption failed", e);
		}
	}

	/**
	 * Decrypts and verifies a payload. If it decrypts successfully, it is 
	 * cryptographically guaranteed to have come from the sender.
	 * 
	 * @param receiverPriv The private key of the recipient.
	 * @param senderPub    The public key of the sender.
	 * @param cipherBytes  The encrypted payload.
	 * @return The decrypted raw data.
	 */
	public static byte[] decrypt(PrivateKey receiverPriv, PublicKey senderPub, byte[] cipherBytes) {
		try {
			CryptoBox box = readCryptoBox(cipherBytes);
			return box.decrypt(receiverPriv, senderPub);
		} catch (Exception e) {
			throw new SecurityException("NetworkBox decryption failed. Payload may be tampered with or keys do not match.", e);
		}
	}

	/**
	 * Computes the X25519 shared secret once.
	 * TODO: Intermediate nodes (B, C) from suggestedRoute should cache the
	 * Key to avoid expensive elliptic curve math on every single hop when
	 * routing high-volume traffic between peers.
	 * 
	 * @param myPriv   The local node's private key.
	 * @param theirPub The remote peer's public key.
	 * @return A shared symmetric Key.
	 */
	public static Key agreeOnSharedSecret(PrivateKey myPriv, PublicKey theirPub) {
		return CryptoBox.agree(myPriv, theirPub);
	}

	/**
	 * Fast-path encryption using a precomputed shared secret.
	 * 
	 * @param sharedSecret The cached Key derived from agreeOnSharedSecret().
	 * @param payload      The data to encrypt.
	 * @return The encrypted payload.
	 */
	public static byte[] encryptFast(Key sharedSecret, byte[] payload) {
		try {
			SecretBox box = SecretBox.encrypt(sharedSecret, payload);
			return toByteArray(box);
		} catch (Exception e) {
			throw new SecurityException("NetworkBox fast-path encryption failed", e);
		}
	}

	/**
	 * Fast-path decryption using a precomputed shared secret.
	 * 
	 * @param sharedSecret The cached Key derived from agreeOnSharedSecret().
	 * @param cipherBytes  The encrypted payload.
	 * @return The decrypted raw data.
	 */
	public static byte[] decryptFast(Key sharedSecret, byte[] cipherBytes) {
		try {
			SecretBox box = readSecretBox(cipherBytes);
			return box.decrypt(sharedSecret);
		} catch (Exception e) {
			throw new SecurityException("NetworkBox fast-path decryption failed. Payload may be tampered.", e);
		}
	}

	private static byte[] toByteArray(CryptoBox box) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			box.writeTo(out);
			return out.toByteArray();
		}
	}

	private static byte[] toByteArray(SecretBox box) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			box.writeTo(out);
			return out.toByteArray();
		}
	}

	private static CryptoBox readCryptoBox(byte[] bytes) throws IOException {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			return CryptoBox.readFrom(in);
		}
	}

	private static SecretBox readSecretBox(byte[] bytes) throws IOException {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			return SecretBox.readFrom(in);
		}
	}
}
