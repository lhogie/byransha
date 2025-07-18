package byransha.web;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache extends BNode {

    private  final Map<String, Entry> map =
            new ConcurrentHashMap<>();
    private  final long MAX_CACHE_SIZE = 50 * 1024 * 1024;
    private  long currentCacheSize = 0;

    protected FileCache(BBGraph g) {
        super(g);
    }

    public Entry get(String cacheKey) {
        return map.get(cacheKey);
    }

    @Override
    public String whatIsThis() {
        return "a cache for small files served by the web server";
    }

    @Override
    public String prettyName() {
        return "file cache";
    }

    public static class V extends NodeEndpoint<FileCache>{

        public V(BBGraph db) {
            super(db);
        }

        @Override
        public EndpointJsonResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, FileCache node) throws Throwable {
            var n = new ObjectNode(null);
            n.set("size", new TextNode(""+node.currentCacheSize));
            n.set("#entries", new IntNode(node.map.size()));
            return new EndpointJsonResponse(n, this);
        }

        @Override
        public String whatItDoes() {
            return "";
        }
    }

    static class Entry  extends BNode{
        final byte[] content;
        final String contentType;
        final long lastModified;
        final String eTag;
        long lastAccessed;

        Entry(byte[] content, String contentType, long lastModified, BBGraph g) {
            super(g);
            this.content = content;
            this.contentType = contentType;
            this.lastModified = lastModified;
            this.eTag =
                    "\"" +
                            Integer.toHexString(java.util.Arrays.hashCode(content)) +
                            "\"";
            this.lastAccessed = System.currentTimeMillis();
        }

        long size() {
            return content.length;
        }

        void updateLastAccessed() {
            this.lastAccessed = System.currentTimeMillis();
        }

        @Override
        public String whatIsThis() {
            return "";
        }

        @Override
        public String prettyName() {
            return "";
        }
    }

    /**
     * Adds a file to the cache, evicting least recently used files if necessary.
     *
     * @param key The cache key (usually the file path)
     * @param content The file content
     * @param contentType The content type of the file
     * @param lastModified The last modified timestamp of the file
     */
      synchronized void add(
            String key,
            byte[] content,
            String contentType,
            long lastModified
    ) {
        // do not cache file bigger than 5MB
        if (content.length > 5 * 1024 * 1024) {
            return;
        }

        Entry existing = map.remove(key);

        if (existing != null) {
            currentCacheSize -= existing.size();
        }

        Entry newEntry = new Entry(
                content,
                contentType,
                lastModified,graph
        );

        while (
                currentCacheSize + newEntry.size() > MAX_CACHE_SIZE &&
                        !map.isEmpty()
        ) {
            String lruKey = null;
            long oldestAccess = Long.MAX_VALUE;

            for (Map.Entry<String, Entry> entry : map.entrySet()) {
                if (entry.getValue().lastAccessed < oldestAccess) {
                    oldestAccess = entry.getValue().lastAccessed;
                    lruKey = entry.getKey();
                }
            }

            if (lruKey != null) {
                Entry evicted = map.remove(lruKey);
                if (evicted != null) {
                    currentCacheSize -= evicted.size();
                }
            }
        }

        map.put(key, newEntry);
        currentCacheSize += newEntry.size();
    }

}
