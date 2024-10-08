package ch.acanda.maven.springbanner;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * This class provides a way to list the files in the root path of
 * spring-banner-plugin. If the plugin is package in a jar file, then the root
 * path is the root of the jar file. If the plugin is not in a jar file (e.g.
 * during development), then the root path is the working directory.
 */
public class RootPath {

    private static final String JAR_URI_SCHEME = "jar";

    private final ReentrantLock lock = new ReentrantLock();

    public Stream<Path> walkReadableFiles(final Class<?> resourceClass,
                                          final String extension) throws IOException {
        return Files.walk(getRootResource(resourceClass), 1)
                    .filter(path -> isReadableFont(path, extension));
    }

    @SuppressWarnings("java:S2095")
    private Path getRootResource(final Class<?> resourceClass) throws IOException {
        final URI uri = URI.create(resourceClass.getResource("/standard.flf").toString());
        if (JAR_URI_SCHEME.equals(uri.getScheme())) {
            @SuppressWarnings("PMD.CloseResource")
            FileSystem fileSystem;
            lock.lock();
            try {
                fileSystem = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                try {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                } catch (FileSystemAlreadyExistsException fsaee) {
                    fileSystem = FileSystems.getFileSystem(uri);
                }
            } finally {
                lock.unlock();
            }
            return fileSystem.getPath("/");
        }
        return Paths.get(uri).getParent();
    }

    private boolean isReadableFont(final Path path, final String extension) {
        return path.toString().endsWith(extension) && Files.isReadable(path) && Files.isRegularFile(path);
    }

}
