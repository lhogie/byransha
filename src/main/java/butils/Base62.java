package butils;

public class Base62 {

    private static final String CHARS = 
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARS.length(); // 62

    public static String encode(long value) {
        if (value == 0) return "0";

        boolean negative = value < 0;
        // Use unsigned handling to avoid Long.MIN_VALUE issues
        long v = negative ? -(value + 1) : value;

        StringBuilder sb = new StringBuilder();
        while (v > 0) {
            sb.append(CHARS.charAt((int)(v % BASE)));
            v /= BASE;
        }
        if (negative) sb.append('-');
        return sb.reverse().toString();
    }
    
   

    public static long decode(String s) {
        if (s.isEmpty()) throw new IllegalArgumentException("Empty string");

        boolean negative = s.charAt(0) == '-';
        int start = negative ? 1 : 0;

        long result = 0;
        for (int i = start; i < s.length(); i++) {
            int digit = CHARS.indexOf(s.charAt(i));
            if (digit == -1) throw new IllegalArgumentException(
                "Invalid character: " + s.charAt(i));
            result = result * BASE + digit;
        }
        return negative ? -(result + 1) : result;
    }

    public static void main(String[] args) {
        long[] tests = { 0, 1, 61, 62, 12345678, Long.MAX_VALUE, Long.MIN_VALUE, -1 };
        for (long n : tests) {
            String encoded = encode(n);
            long decoded = decode(encoded);
            System.out.printf("%22d  →  %-12s  →  %d%n", n, encoded, decoded);
        }
    }
}