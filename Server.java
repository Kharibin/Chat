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

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String name: connectionMap.keySet()) {
                if (!name.equals(userName)){
                connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();

                if (answer.getType() == MessageType.USER_NAME) {

                    if (!answer.getData().isEmpty()) {
                        if (!connectionMap.containsKey(answer.getData())) {
                            connectionMap.put(answer.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return answer.getData();
                        }
                    }
                }
            }
        }
    }
}
