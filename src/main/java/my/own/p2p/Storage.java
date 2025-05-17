package my.own.p2p;

import java.io.InputStream;

public interface Storage {
    public void writeStream(String key, InputStream stream);

    byte[] read(String key);

    void delete(String key);
}

