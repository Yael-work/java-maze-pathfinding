package Client;

import Server.IServerStrategy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private final InetAddress serverIP;
    private final int serverPort;
    private final IServerStrategy strategy;

    public Client(InetAddress serverIP, int serverPort, IServerStrategy strategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.strategy = strategy;
    }

    public void communicateWithServer() {
        try (Socket serverSocket = new Socket(serverIP, serverPort)) {
            strategy.applyStrategy(serverSocket.getInputStream(), serverSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}