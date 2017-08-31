package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port;

        System.out.println("Enter port number");

        port = ConsoleHelper.readInt();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                serverSocket.close();
            } catch (IOException e1) {
                System.out.println("failed to start server, server socket closed");
                e1.printStackTrace();
            }
        }

        while (true){
            try {
                new Handler(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    System.out.println("failed to accept connection, server socket closed");
                    e1.printStackTrace();
                }
            }
        }

    }
    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }
    }
}
