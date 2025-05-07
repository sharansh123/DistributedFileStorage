package my.own.p2p;

import java.io.InputStream;

public interface Storage {
    public void writeStream(String key, InputStream stream);
}

