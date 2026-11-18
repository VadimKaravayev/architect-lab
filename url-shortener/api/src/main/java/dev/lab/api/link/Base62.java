package dev.lab.api.link;

public class Base62 {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String encode(long n) {
        if (n < 0) throw new IllegalArgumentException("n is negative");
        if (n == 0) return "0";

        int abcLength = ALPHABET.length();
        StringBuilder sb = new StringBuilder();

        long m = n;
        while (m > 0) {
            long position = m % abcLength;
            char character = ALPHABET.charAt((int) position);
            sb.append(character);
            m = m / abcLength;
        }

        return sb.reverse().toString();
    }

}
