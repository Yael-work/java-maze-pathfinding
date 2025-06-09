package Client;

import Client.IClientStrategy;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private final InetAddress serverIP;
    private final int serverPort;
    private final IClientStrategy strategy;

    public Client(InetAddress serverIP, int serverPort, IClientStrategy strategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.strategy = strategy;
    }

    public void communicateWithServer() {
        try (Socket serverSocket = new Socket(serverIP, serverPort)) {
            strategy.clientStrategy(serverSocket.getInputStream(), serverSocket.getOutputStream()); // run strategy  using server socket
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}