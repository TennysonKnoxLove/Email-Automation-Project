package email.automation.backend.utils;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final int MAX_ITERATIONS = 2_000_000;
    private static final String PREFIX = "pbkdf2-sha256";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Passwords() {}

    public static String hash(String value) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] digest = pbkdf2(value.toCharArray(), salt, ITERATIONS);
        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(digest);
    }

    public static boolean verify(String value, String stored) {
        if (value == null || stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (iterations < 1 || iterations > MAX_ITERATIONS) {
            return false;
        }
        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expected = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        byte[] actual = pbkdf2(value.toCharArray(), salt, iterations);
        return MessageDigest.isEqual(expected, actual);
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX + "$");
    }

    public static String newCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private static byte[] pbkdf2(char[] value, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(value, salt, iterations, HASH_BITS);
        Arrays.fill(value, '\0');
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not hash password.", e);
        } finally {
            if (spec instanceof PBEKeySpec keySpec) {
                keySpec.clearPassword();
            }
        }
    }
}
