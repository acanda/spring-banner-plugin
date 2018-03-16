package ch.acanda.maven.springbanner;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static ch.acanda.maven.springbanner.GenerateMojo.COLOR_DEFAULT_VALUE;
import static ch.acanda.maven.springbanner.GenerateMojo.INFO_DEFAULT_VALUE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GenerateMojoTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void generateSimpleBanner() throws Exception {
        File bannerFile = folder.newFile("banner.txt");
        GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                             "Hello, World!",
                                             bannerFile.getParentFile(),
                                             bannerFile.getName(),
                                             false,
                                             INFO_DEFAULT_VALUE,
                                             COLOR_DEFAULT_VALUE);

        mojo.execute();

        assertBanner(bannerFile, "banner-simple.txt");
    }

    @Test
    public void generateBannerWithInfo() throws Exception {
        File bannerFile = folder.newFile("banner.txt");
        MavenProject project = new MavenProject();
        project.setVersion("1.2.3");
        GenerateMojo mojo = new GenerateMojo(project,
                                             "Hello, World!",
                                             bannerFile.getParentFile(),
                                             bannerFile.getName(),
                                             true,
                                             INFO_DEFAULT_VALUE,
                                             COLOR_DEFAULT_VALUE);

        mojo.execute();

        assertBanner(bannerFile, "banner-info.txt");
    }

    @Test
    public void generateBannerWithColor() throws Exception {
        File bannerFile = folder.newFile("banner.txt");
        GenerateMojo mojo = new GenerateMojo(new MavenProject(),
                                             "Hello, World!",
                                             bannerFile.getParentFile(),
                                             bannerFile.getName(),
                                             false,
                                             INFO_DEFAULT_VALUE,
                                             "red");

        mojo.execute();

        assertBanner(bannerFile, "banner-color.txt");
    }

    private void assertBanner(File generatedFile, String expectedFile) throws IOException {
        String generatedBanner = Files.toString(generatedFile, Charsets.UTF_8);
        InputStream expectedFileStream = GenerateMojoTest.class.getResourceAsStream(expectedFile);
        try (Reader expectedFileReader = new InputStreamReader(expectedFileStream, Charsets.UTF_8)) {
            String expectedBanner = CharStreams.toString(expectedFileReader)
                                               .replaceAll("\\r\\n", "\n");
            assertThat(generatedBanner, is(expectedBanner));
        }
    }

}
