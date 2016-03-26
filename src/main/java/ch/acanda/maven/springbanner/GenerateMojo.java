package ch.acanda.maven.springbanner;

import com.github.lalyos.jfiglet.FigletFont;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateMojo extends AbstractMojo {

    @Parameter(property = "banner.text", required = true, defaultValue = "${project.name}")
    private String text;

    @Parameter(property = "banner.outputDirectory", required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(property = "banner.filename", required = true, defaultValue = "banner.txt")
    private String filename;

    @Parameter(property = "banner.includeVersion", defaultValue = "true")
    private boolean includeVersion;

    @Parameter(property = "banner.version", defaultValue = "${project.version}")
    private String version;

    @Parameter(property = "banner.color", defaultValue = "default")
    private String color;

    public GenerateMojo() {
        // this constructor is used by maven to create the mojo
    }

    /**
     * This constructor can be used to set all the parameters of the mojo.
     */
    public GenerateMojo(final String text,
                        final File outputDirectory,
                        final String filename,
                        final boolean includeVersion,
                        final String version,
                        final String color) {
        this.text = text;
        this.outputDirectory = outputDirectory;
        this.filename = filename;
        this.includeVersion = includeVersion;
        this.version = version;
        this.color = color == null ? Color.DEFAULT.name() : color;
    }

    /**
     * Generates the Spring Boot banner. Make sure that all parameters are
     * initialized before calling this method.
     */
    public void execute() throws MojoFailureException {
        try {
            getLog().info("Generating Spring Boot banner...");
            final String banner = generateBanner();
            writeBannerFile(banner);
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private String generateBanner() throws IOException {
        final String rawBanner = FigletFont.convertOneLine(text);
        final String[] lines = rawBanner.split("\n");
        final StringBuilder banner = new StringBuilder(32);
        final int baseline = 4;
        final boolean isDefaultColor = Color.DEFAULT.getTagValue().equals(color);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                banner.append('\n');
            }
            if (!isDefaultColor) {
                Color.nameFromTagValue(color)
                     .ifPresent(name -> banner.append("${AnsiColor.").append(name).append('}'));
            }
            if (includeVersion && i == baseline) {
                banner.append(lines[i]).append(' ').append(version);
            } else {
                banner.append(StringUtils.stripEnd(lines[i], " "));
            }
        }
        if (!isDefaultColor) {
            banner.append("${AnsiColor.DEFAULT}");
        }
        getLog().debug('\n' + banner.toString());
        return banner.toString();
    }

    private void writeBannerFile(final String banner) throws IOException {
        final Path bannerFile = outputDirectory.toPath().resolve(filename);
        getLog().debug("Writing banner to file " + bannerFile);
        outputDirectory.mkdirs();
        Files.write(bannerFile, banner.getBytes("UTF-8"));
    }


}
