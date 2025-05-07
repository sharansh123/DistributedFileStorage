package my.own.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.own.p2p.Peer;

import java.io.IOException;
import java.net.Socket;

@Data
@AllArgsConstructor
public class TCPPeer implements Peer {
    Socket socket;
    Boolean outbound;//True if connection was sent, false if connection was accept

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
