package se.monty.s3cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void zipFolder(Path folder, Path outZip) throws IOException {
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Not a directory: " + folder);
        }

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outZip))) {
            Path base = folder.toAbsolutePath();

            // Important: close the stream from Files.walk()
            try (Stream<Path> walk = Files.walk(base)) {
                walk.filter(Files::isRegularFile).forEach(p -> {
                    try {
                        Path rel = base.relativize(p);
                        // Normalize to forward slashes inside zip
                        String entryName = rel.toString().replace('\\', '/');
                        ZipEntry entry = new ZipEntry(entryName);
                        zos.putNextEntry(entry);
                        try (InputStream in = Files.newInputStream(p)) {
                            in.transferTo(zos);
                        }
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
