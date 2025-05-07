package my.own;

import my.own.models.TCPTransport;
import my.own.p2p.Peer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello, World!");
        Consumer<Socket> noHandshake = socket -> {};
        Consumer<Peer> onPeer = peer -> {};
        TCPTransport tcpTransport = new TCPTransport("127.0.0.1", 4000, noHandshake, onPeer);
        try {
            Thread.ofVirtual().start(
                    () -> {
                        while(true) {
                            try {
                                System.out.println("Read from queue: " + tcpTransport.consume().take());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );
            System.out.println("Starting TCP transport");
            tcpTransport.listen();

        } catch (Exception e) {
            System.err.println("Error while listening on peer: " + e);
        }
    }
}