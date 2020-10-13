package ch.acanda.maven.springbanner;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ch.acanda.maven.springbanner.GenerateMojo.COLOR_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.FONT_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.INFO_DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GenerateMojoTest {

    @Test
    public void generateSimpleBanner(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE);

        mojo.execute();

        assertBanner(bannerFile, "banner-simple.txt");
    }

    @Test
    public void generateBannerWithInfo(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final MavenProject project = new MavenProject();
        project.setVersion("1.2.3");
        final GenerateMojo mojo = new GenerateMojo(project,
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   true,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   COLOR_DEFAULT_VALUE);

        mojo.execute();

        assertBanner(bannerFile, "banner-info.txt");
    }

    @Test
    public void generateBannerWithColor(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   FONT_DEFAULT_VALUE,
                                                   "red");

        mojo.execute();

        assertBanner(bannerFile, "banner-color.txt");
    }

    @Test
    public void generateBannerWithCustomFontFile(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "file:src/test/resources/chunky.flf",
                                                   COLOR_DEFAULT_VALUE);

        mojo.execute();

        assertBanner(bannerFile, "banner-font.txt");
    }

    @Test
    public void generateBannerWithMissingCustomFontFile(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "file:src/test/resources/missing.flf",
                                                   COLOR_DEFAULT_VALUE);

        final Path font = Paths.get("src/test/resources/missing.flf");
        assertThatThrownBy(mojo::execute).isExactlyInstanceOf(MojoFailureException.class)
                                         .hasMessage("Font file %s does not exist.", font);
    }

    @Test
    public void generateBannerWithMissingBuiltInFont(@TempDir final Path folder) throws Exception {
        final Path bannerFile = folder.resolve("banner.txt");
        final GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                                   "Hello, World!",
                                                   bannerFile.getParent().toFile(),
                                                   bannerFile.getFileName().toString(),
                                                   false,
                                                   INFO_DEFAULT_VALUE,
                                                   "foo",
                                                   COLOR_DEFAULT_VALUE);

        assertThatThrownBy(mojo::execute).isExactlyInstanceOf(MojoFailureException.class)
                                         .hasMessage("Built-in font foo does not exist. Available fonts: condensed.");
    }

    private void assertBanner(final Path generatedFile, final String expectedFile) throws IOException {
        final InputStream expectedFileStream = GenerateMojoTest.class.getResourceAsStream(expectedFile);
        Assertions.assertThat(expectedFileStream).hasBinaryContent(Files.readAllBytes(generatedFile));
    }

}
