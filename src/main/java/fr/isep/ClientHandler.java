package fr.isep;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ZookeeperClient zkClient;

    public ClientHandler(Socket socket, ZookeeperClient zkClient) {
        this.clientSocket = socket;
        this.zkClient = zkClient;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("set:")) {
                    if (zkClient.isLeader()) {
                        // 如果是领导者，处理设置数字
                        int number = Integer.parseInt(inputLine.split(":")[1]);
                        zkClient.setNumber(number);
                        out.println("Number set to " + number);
                    } else {
                        // 如果不是领导者，通知客户端
                        out.println("Cannot set number, not the leader.");
                    }
                } else if (inputLine.startsWith("guess:")) {
                    // 猜测数字
                    int guess = Integer.parseInt(inputLine.split(":")[1]);
                    int actualNumber = zkClient.getNumber();
                    // 添加更多游戏逻辑，比如比较猜测和实际数字
                    out.println("Your guess: " + guess + ", Actual number: " + actualNumber);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
