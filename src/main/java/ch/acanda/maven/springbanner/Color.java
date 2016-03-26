package ch.acanda.maven.springbanner;

import java.util.Optional;
import java.util.stream.Stream;

public enum Color {
    DEFAULT("default"),
    BLACK("black"),
    RED("red"),
    GREEN("green"),
    YELLOW("yellow"),
    BLUE("blue"),
    MAGENTA("magenta"),
    CYAN("cyan"),
    WHITE("white"),
    BRIGHT_BLACK("bright black"),
    BRIGHT_RED("bright red"),
    BRIGHT_GREEN("bright green"),
    BRIGHT_YELLOW("bright yellow"),
    BRIGHT_BLUE("bright blue"),
    BRIGHT_MAGENTA("bright magenta"),
    BRIGHT_CYAN("bright cyan"),
    BRIGHT_WHITE("bright white");

    /**
     * The value of the configuration's color tag.
     */
    private final String tagValue;

    Color(final String tagValue) {
        assert tagValue != null;
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
    }

    /**
     * Returns the {@code Color} matching the given tag value if it exists.
     * @param tagValue The value of the color tag.
     */
    public static Optional<Color> nameFromTagValue(final String tagValue) {
        return Stream.of(Color.values())
                     .filter(c -> c.tagValue.equals(tagValue))
                     .findFirst();
    }
}
