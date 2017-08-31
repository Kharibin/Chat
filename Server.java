package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        for (Connection connection: connectionMap.values()) {
            try {
                connection.send(message);
            }
            catch (IOException e){
                System.out.println("cant send message to " + connection.getRemoteSocketAddress());
            }
        }
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
            System.out.println("Сервер запущен");
            while (true) {
                new Handler(serverSocket.accept()).start();
                /*Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }
    }
}
