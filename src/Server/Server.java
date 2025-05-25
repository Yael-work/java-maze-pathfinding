package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final int listeningIntervalMS;
    private final IServerStrategy strategy;
    private volatile boolean stop;
    private ExecutorService threadPool;

    public Server(int port, int listeningIntervalMS, IServerStrategy strategy) {
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.strategy = strategy;
    }

    public void start() {
        stop = false;
        threadPool = Executors.newFixedThreadPool(Configurations.getInstance().getThreadPoolSize());

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                serverSocket.setSoTimeout(listeningIntervalMS);
                while (!stop) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.execute(() -> {
                            try {
                                strategy.applyStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException ignored) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        stop = true;
        if (threadPool != null)
            threadPool.shutdown();
    }
}