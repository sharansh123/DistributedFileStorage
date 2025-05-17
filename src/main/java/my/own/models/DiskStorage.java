package my.own.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.own.p2p.Storage;
import my.own.utils.Hasher;

import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
            String fileNameHash = Hasher.hash(key.getBytes());
            Files.createDirectories(p);
            String file = path + File.separator + fileNameHash;

            long n = Files.copy(stream, Paths.get(file));
            logger.info(n + " bytes written to " + file);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.severe("Failed to write: Either path already exists or data is corrupted");
        }
    }

    @Override
    public byte[] read(String key) {
        String path = this.storeOpts.transform.apply(key);
        byte[] readData = null;
        try {
            String fileNameHash = Hasher.hash(key.getBytes());
            String file = path + File.separator + fileNameHash;
            FileInputStream fis = new FileInputStream(file);
            logger.info(" bytes read from " + file);
            readData = fis.readAllBytes();
            fis.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.severe("Failed to write: Either path already exists or data is corrupted");
        }
        return readData;
    }

    @Override
    public void delete(String key) {
        String path = this.storeOpts.transform.apply(key);
        try {
            String fileNameHash = Hasher.hash(key.getBytes());
            String file = path + File.separator + fileNameHash;
            Files.deleteIfExists(Paths.get(file));
            deleteObsolete(path);
            logger.info(" bytes deleted from " + path);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.severe("Failed to delete: Either path already exists or data is corrupted");
        }
    }

    public void deleteObsolete(String path) throws IOException {
        String[] directories = path.split("/");
        String[] paths = new String[directories.length];
        String p = directories[0];
        paths[0] = p;
        for (int i = 1; i < directories.length; i++) {
            p = p + "/" + directories[i];
            paths[i] = p;
        }
        List<String> files = Arrays.asList(paths);
        Collections.reverse(files);
        for (String file : files) {
            if(!Files.newDirectoryStream(Paths.get(file)).iterator().hasNext()) Files.deleteIfExists(Paths.get(file));
        }
    }

}
