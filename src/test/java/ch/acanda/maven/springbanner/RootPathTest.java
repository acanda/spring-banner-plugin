package ch.acanda.maven.springbanner;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RootPathTest {

    @Test
    public void listBuiltInFonts() throws IOException, URISyntaxException {
        try (RootPath rootPath = new RootPath()) {
            final Stream<String> rootFiles = rootPath.walkReadableFiles(".flf").map(p -> p.getFileName().toString());
            assertThat(rootFiles).containsExactly("condensed.flf");
        }
    }

}
