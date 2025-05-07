package my.own.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.own.p2p.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.logging.Logger;

@AllArgsConstructor
@Data
public class DiskStorage implements Storage {
    StoreOpts storeOpts;
    public final static Logger logger = Logger.getLogger(DiskStorage.class.getName());

    @Override
    public void writeStream(String key, InputStream stream) {
        String path = this.storeOpts.transform.apply(key);
        Path p = Paths.get(path);
        try {
            Files.createDirectories(p);
            String fileName = "tempName";
            String file = path + File.separator + fileName;
            long n = Files.copy(stream, Paths.get(file));
            logger.info(n + " bytes written to " + file);
        } catch (IOException e) {
            logger.severe("Failed to write: Either path already exists or data is corrupted");
        }
    }
}
