package ch.acanda.maven.springbanner;

import java.util.stream.Stream;

final class Whitespace {

    private Whitespace() {
        // hide constructor of utility class
    }

    /**
     * Strips whitespace around a banner, i.e.
     * <ul>
     *     <li>removes blank lines at the top and bottom</li>
     *     <li>removes whitespace columns at the start of the lines</li>
     *     <li>removes all whitespace at the end of each line</li>
     * </ul>
     */
    public static String[] strip(final String banner) {
        final String[] lines = stripEnd(banner).split("\\s*\n");
        final int firstLine = firstNonEmptyLine(lines);
        final int lastLine = lastNonEmptyLine(lines);
        final int stripStart = countStrippableWhitespaceAtStart(lines);
        return Stream.of(lines)
                     .skip(firstLine)
                     .map(line -> line.substring(Math.min(line.length(), stripStart)))
                     .limit(lastLine - firstLine + 1)
                     .toArray(String[]::new);
    }

    private static String stripEnd(final String s) {
        final int end = s.length() - 1;
        if (end == -1 || s.charAt(end) != ' ') {
            return s;
        }
        for (int i = end - 1; i >= 0; i--) {
            if (s.charAt(i) != ' ') {
                return s.substring(0, i + 1);
            }
        }
        return "";
    }

    private static int firstNonEmptyLine(final String... lines) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > 0) {
                return i;
            }
        }
        return lines.length;
    }

    private static int lastNonEmptyLine(final String... lines) {
        for (int i = lines.length - 1; i >= 0; i--) {
            if (lines[i].length() > 0) {
                return i;
            }
        }
        return 0;
    }

    private static int countStrippableWhitespaceAtStart(final String... lines) {
        return Stream.of(lines)
                     .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                     .mapToInt(Whitespace::countStrippableWhitespaceAtStart)
                     .min()
                     .orElse(0);
    }

    private static int countStrippableWhitespaceAtStart(final String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                return i;
            }
        }
        return 0;
    }

}
