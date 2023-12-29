package logger;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility functions to work with strings
 */
public interface StringUtils {
    /**
     * Centers a given string in the given space, with possibly one more character to the right.
     * It is assumed the amount of space is bigger than the string.
     *
     * @param s    The string to center
     * @param size The space it has to be centered on
     * @return A string containing the original string surrounded by the correct number of spaces to center it
     */
    static String center(String s, int size) {
        if (s == null || size <= s.length()) return s;

        StringBuilder sb = new StringBuilder(size);
//		sb.append(" ".repeat((size - s.length()) / 2));
        sb.append(nChars(' ', (size - s.length()) / 2));
        sb.append(s);
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Repeates the given character n times.
     *
     * @param c The character to be repeated
     * @param n The number of times to repeat it
     * @return The character repeated n times
     */
    static String nChars(char c, int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> String.valueOf(c))
                .collect(Collectors.joining());
    }


    /**
     * Converts a SCREAMING_SNAKE_CASE string in into a camelCase string.
     * Use it to convert method enum elements into their respective method names
     *
     * @param input The SCREAMING_SNAKE_CASE string
     * @return The string in camelCase
     */
    static String convertToCamelCase(String input) {
        String[] parts = input.toLowerCase().split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            camelCase.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));
        }
        return camelCase.toString();
    }
}
