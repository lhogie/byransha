package byransha.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {

	private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final BigInteger BASE = BigInteger.valueOf(62);
	private static final int TARGET_LENGTH = 22; // Maximum length of Base62 128-bit integer

	/**
	 * Encodes a UUID to a Base62 string.
	 * 
	 * @param uuid         The UUID to encode
	 * @param padTo22Chars If true, pads with leading '0' characters to maintain a
	 *                     fixed 22-char string length
	 */
	public static String encode(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID cannot be null");
		}

		// Extract 16 bytes (128 bits) from the UUID
		ByteBuffer bb = ByteBuffer.allocate(16);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		// Create positive BigInteger from 16 bytes
		BigInteger number = new BigInteger(1, bb.array());
		if (number.equals(BigInteger.ZERO)) {
			return  "0";
		}

		StringBuilder sb = new StringBuilder();
		while (number.compareTo(BigInteger.ZERO) > 0) {
			BigInteger[] divRem = number.divideAndRemainder(BASE);
			sb.append(ALPHABET.charAt(divRem[1].intValue()));
			number = divRem[0];
		}

		String result = sb.reverse().toString();

		return result;
	}

	/**
	 * Decodes a Base62 string back into a UUID.
	 */
	public static UUID decode(String base62) {
		if (base62 == null || base62.isEmpty()) {
			throw new IllegalArgumentException("Base62 string cannot be null or empty");
		}

		BigInteger number = BigInteger.ZERO;
		for (int i = 0; i < base62.length(); i++) {
			char c = base62.charAt(i);
			int digit = ALPHABET.indexOf(c);
			if (digit == -1) {
				throw new IllegalArgumentException("Invalid Base62 character: " + c);
			}
			number = number.multiply(BASE).add(BigInteger.valueOf(digit));
		}

		byte[] rawBytes = number.toByteArray();
		byte[] uuidBytes = new byte[16];

		// BigInteger may return >16 bytes due to sign bit or <16 bytes due to leading
		// zeros
		if (rawBytes.length > 16) {
			System.arraycopy(rawBytes, rawBytes.length - 16, uuidBytes, 0, 16);
		} else {
			System.arraycopy(rawBytes, 0, uuidBytes, 16 - rawBytes.length, rawBytes.length);
		}

		ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
		long mostSig = bb.getLong();
		long leastSig = bb.getLong();

		return new UUID(mostSig, leastSig);
	}

	public static void main(String[] args) {
		UUID originalUuid = UUID.randomUUID();

		// Encode
		String base62Padded = encode(originalUuid);

		// Decode
		UUID decodedUuid = decode(base62Padded);

		System.out.println("Original UUID:  " + originalUuid);
		System.out.println("Base62 (22-char): " + base62Padded);
		System.out.println("Decoded UUID:   " + decodedUuid);

		assert originalUuid.equals(decodedUuid) : "UUID decoding mismatch!";
	}
}
