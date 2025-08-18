package byransha;

import java.lang.reflect.Field;

public class Utils {
    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw new NoSuchFieldException(
                        "Field '" + fieldName + "' not found in class " + clazz.getName()
                );
            }
            return findField(superClass, fieldName);
        }
    }

   public static String mimeType(String url) {
        if (url == null) return "application/octet-stream";

        String lowerUrl = url.toLowerCase();

        if (lowerUrl.endsWith(".html") || lowerUrl.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        } else if (lowerUrl.endsWith(".js") || lowerUrl.endsWith(".jsx")) {
            return "text/javascript; charset=utf-8";
        } else if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerUrl.endsWith(".png")) {
            return "image/png";
        } else if (lowerUrl.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerUrl.endsWith(".css")) {
            return "text/css; charset=utf-8";
        } else if (lowerUrl.endsWith(".json")) {
            return "application/json; charset=utf-8";
        } else if (lowerUrl.endsWith(".txt")) {
            return "text/plain; charset=utf-8";
        } else if (lowerUrl.endsWith(".ico")) {
            return "image/x-icon";
        } else if (
                lowerUrl.endsWith(".webmanifest") || lowerUrl.endsWith(".manifest")
        ) {
            return "application/manifest+json";
        } else if (lowerUrl.endsWith(".ttf")) {
            return "font/ttf";
        } else if (lowerUrl.endsWith(".woff")) {
            return "font/woff";
        } else if (lowerUrl.endsWith(".woff2")) {
            return "font/woff2";
        } else {
            System.err.println(
                    "Warning: Unknown MIME type for file: " +
                            url +
                            ". Defaulting to application/octet-stream."
            );
            return "application/octet-stream";
        }
    }
}
