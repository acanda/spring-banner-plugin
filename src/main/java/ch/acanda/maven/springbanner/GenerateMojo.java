package ch.acanda.maven.springbanner;

import com.github.lalyos.jfiglet.FigletFont;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateMojo extends AbstractMojo {

    public static final String TEXT_DEFAULT_VALUE = "${project.name}";
    public static final String OUTPUT_DIRECTORY_DEFAULT_VALUE = "${project.build.outputDirectory}";
    public static final String FILENAME_DEFAULT_VALUE = "banner.txt";
    public static final String INCLUDE_INFO_DEFAULT_VALUE = "true";
    public static final String COLOR_DEFAULT_VALUE = "default";
    public static final String INFO_DEFAULT_VALUE =
            "Version: ${application.version:${project.version}}, "
            + "Server: ${server.address:localhost}:${server.port:8080}, "
            + "Active Profiles: ${spring.profiles.active:none}";

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "banner.text", required = true, defaultValue = TEXT_DEFAULT_VALUE)
    private String text;

    @Parameter(property = "banner.outputDirectory", required = true, defaultValue = OUTPUT_DIRECTORY_DEFAULT_VALUE)
    private File outputDirectory;

    @Parameter(property = "banner.filename", required = true, defaultValue = FILENAME_DEFAULT_VALUE)
    private String filename;

    @Parameter(property = "banner.includeInfo", defaultValue = INCLUDE_INFO_DEFAULT_VALUE)
    private boolean includeInfo;

    @Parameter(property = "banner.info", defaultValue = INFO_DEFAULT_VALUE)
    private String info;

    @Parameter(property = "banner.color", defaultValue = COLOR_DEFAULT_VALUE)
    private String color;

    public GenerateMojo() {
        // this constructor is used by maven to create the mojo
    }

    /**
     * This constructor can be used to set all the parameters of the mojo.
     */
    public GenerateMojo(final MavenProject project,
                        final String text,
                        final File outputDirectory,
                        final String filename,
                        final boolean includeInfo,
                        final String info,
                        final String color) {
        this.project = project;
        this.text = text;
        this.outputDirectory = outputDirectory;
        this.filename = filename;
        this.includeInfo = includeInfo;
        this.info = info;
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
        final InputStream fontStream = GenerateMojo.class.getResourceAsStream("/condensed.flf");
        final String rawBanner = FigletFont.convertOneLine(fontStream, text);
        final String[] lines = rawBanner.split("\n");
        final StringBuilder banner = new StringBuilder(32);
        final boolean isDefaultColor = Color.DEFAULT.getTagValue().equals(color);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                banner.append('\n');
            }
            if (!isDefaultColor) {
                Color.nameFromTagValue(color)
                     .ifPresent(name -> banner.append("${AnsiColor.").append(name).append('}'));
            }
            banner.append(StringUtils.stripEnd(lines[i], " "));
        }
        if (!isDefaultColor) {
            banner.append("${AnsiColor.DEFAULT}");
        }
        if (includeInfo) {
            info = info == null ? null : info.replaceAll("\\$\\{project\\.version\\}", project.getVersion());
            banner.append('\n').append(info);
        }
        banner.append('\n');
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
