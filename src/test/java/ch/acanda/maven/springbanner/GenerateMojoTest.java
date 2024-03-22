package ch.acanda.maven.springbanner;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ch.acanda.maven.springbanner.GenerateMojo.COLOR_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.FILENAME_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.FONT_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.INFO_DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerateMojoTest {

    public static final String DEFAULT_TEXT = "Hello, World!";

    @Test
    void generateSimpleBanner(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        mojo.execute();

        assertBanner(bannerFile, "banner-simple.txt");
    }

    @Test
    void generateBannerWithInfo(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final MavenProject project = new MavenProject();
        project.setVersion("1.2.3");
        final GenerateMojo mojo = new GenerateMojo(project,
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   true,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        mojo.execute();

        assertBanner(bannerFile, "banner-info.txt");
    }

    @Test
    void generateBannerWithColor(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   "red",
                                                   false);

        mojo.execute();

        assertBanner(bannerFile, "banner-color.txt");
    }

    @Test
    void generateBannerWithCustomFontFile(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "file:src/test/resources/chunky.flf",
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        mojo.execute();

        assertBanner(bannerFile, "banner-font.txt");
    }

    @Test
    void generateBannerWithStrippedWhitespace(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "<->",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        mojo.execute();
        assertBanner(bannerFile, "banner-stripped.txt");
    }

    @Test
    void generateBannerWithNonBreakingSpace(@TempDir final Path folder) throws MojoFailureException, IOException {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   true,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE,
                                                   true);

        mojo.execute();

        assertBanner(bannerFile, "banner-nbsp.txt");
    }


    @Test
    void generateBannerWithMissingCustomFontFile(@TempDir final Path folder) {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "file:src/test/resources/missing.flf",
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        final Path font = Paths.get("src/test/resources/missing.flf");
        assertThatThrownBy(mojo::execute).isExactlyInstanceOf(MojoFailureException.class)
                                         .hasMessage("Font file %s does not exist.", font);
    }

    @Test
    void generateBannerWithMissingBuiltInFont(@TempDir final Path folder) {
        final Path bannerFile = folder.resolve(FILENAME_DEFAULT_VALUE);
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   DEFAULT_TEXT,
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "foo",
                                                   COLOR_DEFAULT_VALUE,
                                                   false);

        assertThatThrownBy(mojo::execute).isExactlyInstanceOf(MojoFailureException.class)
                                         .hasMessage("The built-in font \"foo\" does not exist. Available fonts:"
                                                     + " banner, big, block, bubble, digital, ivrit, lean, mini,"
                                                     + " mnemonic, script, shadow, slant, small, smscript, smshadow,"
                                                     + " smslant, standard, term.");
    }

    private void assertBanner(final Path generatedFile, final String expectedFile) throws IOException {
        try (InputStream stream = GenerateMojoTest.class.getResourceAsStream(expectedFile)) {
            final String generatedBanner = Files.readString(generatedFile);
            final String expectedBanner = IOUtil.toString(stream, "UTF-8");
            assertThat(generatedBanner).isEqualTo(expectedBanner);
        }
    }

}
