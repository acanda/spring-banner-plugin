package ch.acanda.maven.springbanner;

import com.github.dtmo.jfiglet.FigFontResources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RootPathTest {

    @Test
    void listBuiltInFonts() throws IOException, URISyntaxException {
        final RootPath rootPath = new RootPath();
        final Stream<String> rootFiles = rootPath.walkReadableFiles(FigFontResources.class, ".flf")
                                                 .map(p -> p.getFileName().toString());
        assertThat(rootFiles).containsExactly("term.flf",
                                              "standard.flf",
                                              "smslant.flf",
                                              "smshadow.flf",
                                              "smscript.flf",
                                              "small.flf",
                                              "slant.flf",
                                              "shadow.flf",
                                              "script.flf",
                                              "mnemonic.flf",
                                              "mini.flf",
                                              "lean.flf",
                                              "ivrit.flf",
                                              "digital.flf",
                                              "bubble.flf",
                                              "block.flf",
                                              "big.flf",
                                              "banner.flf");

    }

    @Test
    void listBuiltInFontsMultiThreaded() {
        // Assert that using RootPath in a multithreaded environment does not
        // throw file system exceptions like FileSystemAlreadyExistsException.
        assertDoesNotThrow(() -> Stream
                .generate(RootPath::new)
                .limit(100)
                .parallel()
                .map(rootPath -> {
                    try {
                        return rootPath.walkReadableFiles(FigFontResources.class, ".flf").toList();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .toList()
        );
    }

}
