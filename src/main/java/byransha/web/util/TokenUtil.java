package byransha.web.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private static final int TOKEN_LENGTH_BYTES = 32; // OWASP recommendation: >= 128 bits (16 bytes), 32 is safer

    /**
     * Generates a cryptographically secure, URL-safe random token.
     * @return A secure token string.
     */
    public static String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH_BYTES];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}