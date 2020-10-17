package ch.acanda.maven.springbanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * This class provides a way to list the files in the root path of
 * spring-banner-plugin. If the plugin is package in a jar file, then the root
 * path is the root of the jar file. If the plugin is not in a jar file (e.g.
 * during development), then the root path is the working directory.
 */
public class RootPath implements AutoCloseable {

    private FileSystem fileSystem;

    public Stream<Path> walkReadableFiles(final String extension) throws IOException, URISyntaxException {
        return Files.walk(getRootResource(), 1)
                    .filter(path -> isReadableFont(path, extension));
    }

    @Override
    public void close() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    private Path getRootResource() throws IOException, URISyntaxException {
        final URI uri = RootPath.class.getResource("/condensed.flf").toURI();
        if ("jar".equals(uri.getScheme())) {
            fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            return fileSystem.getPath("/");
        }
        return Paths.get(uri).getParent();
    }

    private boolean isReadableFont(final Path path, final String extension) {
        return path.toString().endsWith(extension) && Files.isReadable(path) && Files.isRegularFile(path);
    }

}
