package my.own.models;

import my.own.exceptions.PeerRejectedException;
import my.own.p2p.Peer;
import my.own.p2p.Transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class TCPTransport implements Transport {
    String listenAddress;
    int listenPort;
    ServerSocket serverSocket;
    WriteLock writeLock;
    ReadLock readLock;
    Map<Socket, Peer> peers;
    ArrayBlockingQueue<String> channel;
    Consumer<Socket> handshake;
    Consumer<Peer> onPeer;

    public final static Logger logger = Logger.getLogger(TCPTransport.class.getName());

    public TCPTransport(String listenAddress, int listenPort, Consumer<Socket> handshake, Consumer<Peer> onPeer) {
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        peers = new HashMap<>();
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
        this.handshake = handshake;
        this.channel = new ArrayBlockingQueue<String>(100, true);
        this.onPeer = onPeer;
    }

    public void listen() throws IOException {
        this.serverSocket = new ServerSocket(listenPort);
        while(true){
            try {
                Socket socket = serverSocket.accept();
                this.handshake.accept(socket);
                this.onPeer.accept(new TCPPeer(socket, true));
                Thread.ofVirtual().start(() -> {
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        logger.severe("Could not handle connection");
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                logger.severe("Could not accept connection");
            }
        }
    }

    public void handleConnection(Socket socket) throws IOException {
        System.out.println("Received connection from " + socket.getRemoteSocketAddress());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        while(true) {
            String line = in.readLine();
            this.channel.add(line);
            printWriter.println("Received: " + line);
            printWriter.flush();
        }
    }

    @Override
    public BlockingQueue<String> consume() {
        return this.channel;
    }
}
