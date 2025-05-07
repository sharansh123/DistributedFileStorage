package my.own;

import my.own.models.DiskStorage;
import my.own.models.StoreOpts;
import my.own.p2p.Storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Function;

public class DiskMain {
    public static void main(String[] args) {

        Function<String , String> hasher = (String key) -> {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] hashBytes = digest.digest(key.getBytes());
                String hexPath = HexFormat.of().formatHex(hashBytes);
                int blockSize = 10;
                int paths = hexPath.length() / blockSize;
                String[] allPaths = new String[paths];
                for (int i = 0; i < paths; i++) {
                    allPaths[i] = hexPath.substring(i*blockSize, i*blockSize+blockSize);
                }
                return String.join(File.separator, allPaths);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        };

        StoreOpts opts = new StoreOpts(hasher);
        Storage storage = new DiskStorage(opts);
        String output = "some image bytes";
        byte[] bytes = output.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        storage.writeStream("abc", bais);

    }
}
