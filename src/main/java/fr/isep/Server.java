package fr.isep;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private ZookeeperClient zkClient;

    public Server(int port, String zkConnectString, String nodeId) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(5);
        this.zkClient = new ZookeeperClient(zkConnectString, nodeId);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, zkClient);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8080; // ÿ��������ʵ��Ӧ���в�ͬ�Ķ˿�
        String zkConnectString = "localhost:2181"; // Zookeeper�����ַ���
        String nodeId = "server-" + port; // Ϊÿ��������ʵ������Ψһ�Ľڵ�ID

        Server server = new Server(port, zkConnectString, nodeId);
        server.start();
    }
}
