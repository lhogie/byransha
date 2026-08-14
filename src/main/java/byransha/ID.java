package byransha;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bouncycastle.crypto.digests.KeccakDigest;

import byransha.util.ByUtils;

public class ID implements Externalizable {
	private long a, b;

	public ID(long g, long l) {
		this.a = g;
		this.b = l;
	}

	public ID(ByteBuffer buf) {
		this.a = buf.getLong();
		this.b = buf.getLong();
	}

	static KeccakDigest keccak = new KeccakDigest(128);

	public static ID fromDate(LocalDateTime ldt) {
		long epochSeconds = ldt.toEpochSecond(ZoneOffset.UTC);
		long nanosOfSecond = ldt.getNano();
		return new ID(epochSeconds, nanosOfSecond);
	}

	public ID augmentWith(String s) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2 + s.length());
		buffer.putLong(a);
		buffer.putLong(b);
		var input = buffer.put(s.getBytes()).array();
		keccak.update(input, 0, input.length);
		byte[] hash = new byte[16];
		keccak.doFinal(hash, 0);
		return new ID(ByteBuffer.wrap(hash));
	}

	public ID() {
		this(ByUtils.random.nextLong(), ByUtils.random.nextLong());
	}

	public ID(long global, int localA, int localB) {
		this(global, ((long) localA << 32) | Integer.toUnsignedLong(localB));
	}

	public boolean isLocal() {
		return a == 0;
	}

	private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final BigInteger BASE = BigInteger.valueOf(62);
	private static final int TARGET_LENGTH = 22; // Maximum length of Base62 128-bit integer

	/**
	 * Encodes a UUID to a Base62 string.
	 *
	 * @param uuid
	 *                         The UUID to encode
	 * @param padTo22Chars
	 *                         If true, pads with leading '0' characters to maintain
	 *                         a
	 *                         fixed 22-char string length
	 */
	public String toBase62() {
		// Extract 16 bytes (128 bits) from the UUID
		var bb = toByteBuffer();

		// Create positive BigInteger from 16 bytes
		BigInteger number = new BigInteger(1, bb.array());
		if (number.equals(BigInteger.ZERO)) {
			return "0";
		}

		StringBuilder sb = new StringBuilder();
		while (number.compareTo(BigInteger.ZERO) > 0) {
			BigInteger[] divRem = number.divideAndRemainder(BASE);
			sb.append(ALPHABET.charAt(divRem[1].intValue()));
			number = divRem[0];
		}

		return sb.reverse().toString();
	}

	private ByteBuffer toByteBuffer() {
		ByteBuffer bb = ByteBuffer.allocate(16);
		bb.putLong(a);
		bb.putLong(b);
		return bb;
	}

	/**
	 * Decodes a Base62 string back into a UUID.
	 */
	public static ID fromBase62(String base62) {
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
		return new ID(mostSig, leastSig);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(a);
		out.writeLong(b);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.a = in.readLong();
		this.b = in.readLong();

	}

	@Override
	public String toString() {
		return toBase62();
	}

	@Override
	public boolean equals(Object o) {
		var id = (ID) o;
		return id.a == a && id.b == b;
	}

	@Override
	public int hashCode() {
		return toByteBuffer().hashCode();
	}

	public long getA() {
		return a;
	}

	public long getB() {
		return b;
	}

	public static int compare(ID id, ID id2) {
		return 0;
	}
}
