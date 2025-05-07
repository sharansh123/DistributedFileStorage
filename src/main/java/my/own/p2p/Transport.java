package my.own.p2p;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public interface Transport {
    public void listen() throws IOException;
    public BlockingQueue<String> consume();
}
