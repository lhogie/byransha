package byransha.nodes.system;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon {
    private static final Argon2 i = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String hash(String password) {
        // Paramètres : itérations, mémoire (en KiB), parallélisme
        // Ici : 3 itérations, 64 Mo de RAM, 4 threads
        return i.hash(3, 65536, 1, password.toCharArray());
    }

    public static boolean verify(String hash, String password) {
        return i.verify(hash, password.toCharArray());
    }
}