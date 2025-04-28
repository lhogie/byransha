package byransha.web;

import byransha.User;
import byransha.web.util.TokenUtil;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionStore {

    // OWASP recommendation: Session timeout (e.g., 30 minutes inactivity)
    public static final long INACTIVITY_TIMEOUT_MILLIS = 30 * 60 * 1000; // 30 minutes
    // OWASP recommendation: Absolute session timeout (e.g., 8 hours)
    public static final long ABSOLUTE_TIMEOUT_MILLIS = 8 * 60 * 60 * 1000; // 8 hours

    private final Map<String, SessionData> activeSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    public static record SessionData(
            int userId,
            Instant creationTime,
            Instant lastAccessTime,
            String csrfToken
    ) {
        public SessionData updateLastAccessTime(Instant newAccessTime) {
            return new SessionData(this.userId, this.creationTime, newAccessTime, this.csrfToken);
        }

        // Check if the session is expired based on inactivity
        public boolean isExpiredByInactivity(Instant now) {
            return now.isAfter(lastAccessTime.plusMillis(INACTIVITY_TIMEOUT_MILLIS));
        }

        // Check if the session is expired based on absolute time
        public boolean isExpiredByAbsoluteTime(Instant now) {
            return now.isAfter(creationTime.plusMillis(ABSOLUTE_TIMEOUT_MILLIS));
        }
    }

    public SessionStore() {
        cleanupScheduler.scheduleAtFixedRate(this::removeExpiredSessions, 5, 5, TimeUnit.MINUTES);
        System.out.println("SessionStore initialized with periodic cleanup.");
    }

    /**
     * Creates a new session for the user and returns the session token.
     * OWASP: Regenerate session ID on login.
     * @param user The authenticated user.
     * @param csrfToken The CSRF token for this session.
     * @return The newly generated secure session token.
     */
    public String createSession(User user, String csrfToken) {
        if (user == null || csrfToken == null || csrfToken.isBlank()) {
            throw new IllegalArgumentException("User and CSRF token cannot be null or blank");
        }

        String token = TokenUtil.generateSecureToken();
        Instant now = Instant.now();
        SessionData sessionData = new SessionData(user.id(), now, now, csrfToken);
        activeSessions.put(token, sessionData);

        String tokenPrefix = token.substring(0, Math.min(8, token.length()));
        System.out.printf("[AUTH] Session created for user ID %d with token prefix %s%n", user.id(), tokenPrefix);
        return token;
    }

    /**
     * Retrieves session data for a given token, if valid and not expired.
     * Updates the last access time if the session is valid.
     * @param token The session token.
     * @return An Optional containing the SessionData if valid, otherwise empty.
     */
    public Optional<SessionData> getValidSession(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        SessionData sessionData = activeSessions.get(token);
        if (sessionData == null) {
            return Optional.empty();
        }

        Instant now = Instant.now();
        String tokenPrefix = token.substring(0, Math.min(8, token.length()));

        // OWASP: Check session timeouts
        if (sessionData.isExpiredByInactivity(now)) {
            System.out.printf("Session expired due to inactivity for token prefix %s%n", tokenPrefix);
            activeSessions.remove(token);
            return Optional.empty();
        }
        if (sessionData.isExpiredByAbsoluteTime(now)) {
            System.out.printf("Session expired due to absolute timeout for token prefix %s%n", tokenPrefix);
            activeSessions.remove(token);
            return Optional.empty();
        }

        SessionData updatedSessionData = sessionData.updateLastAccessTime(now);
        activeSessions.put(token, updatedSessionData);

        return Optional.of(updatedSessionData);
    }

    /**
     * Removes a session by its token (logout).
     * @param token The session token to invalidate.
     */
    public void removeSession(String token) {
        if (token != null && !token.isBlank()) {
            if (activeSessions.remove(token) != null) {
                String tokenPrefix = token.substring(0, Math.min(8, token.length()));
                System.out.printf("Session removed for token prefix %s%n", tokenPrefix);
            }
        }
    }

    /**
     * Removes all sessions associated with a specific user ID.
     * Useful if you want to enforce single session or invalidate all on password change.
     * @param userId The user ID whose sessions should be removed.
     */
    public void removeSessionByUserId(int userId) {
        activeSessions.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().userId() == userId;
            if (match) {
                String tokenPrefix = entry.getKey().substring(0, Math.min(8, entry.getKey().length()));
                System.out.printf("Removing session (token prefix: %s) for user ID %d%n", tokenPrefix, userId);
            }
            return match;
        });
    }


    /**
     * Periodically called task to clean up expired sessions.
     */
    private void removeExpiredSessions() {
        Instant now = Instant.now();
        int removedCount = 0;
        for (Map.Entry<String, SessionData> entry : new ConcurrentHashMap<>(activeSessions).entrySet()) {
            if (entry.getValue().isExpiredByInactivity(now) || entry.getValue().isExpiredByAbsoluteTime(now)) {
                if (activeSessions.remove(entry.getKey(), entry.getValue())) {
                    removedCount++;
                    String tokenPrefix = entry.getKey().substring(0, Math.min(8, entry.getKey().length()));
                }
            }
        }
        if (removedCount > 0) {
            System.out.printf("Removed %d expired sessions during cleanup.%n", removedCount);
        }
    }

    public void shutdown() {
        cleanupScheduler.shutdown();
        System.out.println("SessionStore cleanup scheduler shut down.");
    }
}