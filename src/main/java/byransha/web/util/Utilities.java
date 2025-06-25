package byransha.web.util;

/**
 * Utility class providing string comparison and other common functionality.
 */
public class Utilities {

    /**
     * Calculates the Levenshtein distance between two strings.
     * The Levenshtein distance is a measure of the similarity between two strings,
     * defined as the minimum number of single-character edits (insertions, deletions,
     * or substitutions) required to change one string into the other.
     *
     * @param source the first string, must not be null
     * @param target the second string, must not be null
     * @return the Levenshtein distance between the two strings
     * @throws NullPointerException if either source or target is null
     */
    public static int levenshteinDistance(String source, String target) {
        if (source == null || target == null) {
            throw new NullPointerException("Input strings cannot be null");
        }

        // Optimization for common cases
        if (source.equals(target)) {
            return 0;
        }
        if (source.isEmpty()) {
            return target.length();
        }
        if (target.isEmpty()) {
            return source.length();
        }

        // Create matrix of size (source.length()+1) x (target.length()+1)
        int[][] distanceMatrix = new int[source.length() + 1][target.length() + 1];

        // Initialize first row and column
        for (int i = 0; i <= source.length(); i++) {
            distanceMatrix[i][0] = i;
        }

        for (int j = 0; j <= target.length(); j++) {
            distanceMatrix[0][j] = j;
        }

        // Fill the matrix
        for (int i = 1; i <= source.length(); i++) {
            for (int j = 1; j <= target.length(); j++) {
                int substitutionCost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                distanceMatrix[i][j] = Math.min(
                        Math.min(distanceMatrix[i - 1][j] + 1, distanceMatrix[i][j - 1] + 1),
                        distanceMatrix[i - 1][j - 1] + substitutionCost
                );
            }
        }

        return distanceMatrix[source.length()][target.length()];
    }
}