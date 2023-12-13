package fr.isep;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private final String serverAddress;
    private final int serverPort;
    private final String clientId;

    public GameClient(String serverAddress, int serverPort, String clientId) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientId = clientId;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server as " + clientId);

            // 新线程读取服务器响应
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // 主线程发送用户输入到服务器
            while (true) {
                System.out.print("Enter your guess or command: ");
                String userInput = scanner.nextLine();
                out.println(clientId + ":" + userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java GameClient <server_address> <server_port> <client_id>");
            return;
        }
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String clientId = args[2];

        GameClient client = new GameClient(serverAddress, serverPort, clientId);
        client.start();
    }
}
