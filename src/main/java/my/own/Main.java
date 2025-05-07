package my.own;

import my.own.models.TCPTransport;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello, World!");
        Consumer<Socket> noHandshake = socket -> {};
        TCPTransport tcpTransport = new TCPTransport("127.0.0.1", 4000, noHandshake);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}