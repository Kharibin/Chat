package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client extends Thread{
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client1 = new Client();
        client1.run();
    }

    @Override
    public void run() {
       SocketThread socketThread = getSocketThread();
       socketThread.setDaemon(true);
       socketThread.start();
       synchronized (this){
           try {
               this.wait();
           } catch (InterruptedException e) {
               ConsoleHelper.writeMessage("Exception during waiting");
               return;
           }
       }
       while (clientConnected){
           ConsoleHelper.writeMessage("connection established");
           String text = ConsoleHelper.readString();
           if (text.equals("exit")) break;
           if (shouldSendTextFromConsole()) try {
               connection.send(new Message(MessageType.TEXT, text));
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    protected String getServerAddress(){
        ConsoleHelper.writeMessage("Enter server address");
        return ConsoleHelper.readString();
    }

    protected int getServerPort(){
        ConsoleHelper.writeMessage("Enter server port");
        return ConsoleHelper.readInt();
    }

    protected String getUserName(){
        ConsoleHelper.writeMessage("Enter user name");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    protected void sendTextMessage(String text){
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("error sending message, u will be disconnected");
            clientConnected = false;
        }
    }

    public class SocketThread extends Thread{

    }
}
