package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;

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
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("User enterd chat: " + userName);
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Ladies and Gentleman " + userName + " has left the building");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            synchronized (Client.this) {
                Client.this.clientConnected = clientConnected;
                Client.this.notify();
            }

        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            Message message;
            while (!clientConnected) {
                try {
                    message = connection.receive();
                } catch (ClassNotFoundException e) {
                    throw new IOException("Unexpected MessageType");
                }
                if (message.getType() == MessageType.NAME_REQUEST) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else {
                    if (message.getType() == MessageType.NAME_ACCEPTED) {notifyConnectionStatusChanged(true);}
                    else throw new IOException("Unexpected MessageType");}

            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                MessageType thisType = message.getType();
                String thisData = message.getData();
                if (thisType == MessageType.TEXT) processIncomingMessage(thisData);
                if (thisType == MessageType.USER_ADDED) informAboutAddingNewUser(thisData);
                if (thisType == MessageType.USER_REMOVED) informAboutDeletingNewUser(thisData);
                if (thisType != MessageType.TEXT &&
                        thisType != MessageType.USER_ADDED && thisType != MessageType.USER_REMOVED) {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }



    }
}
