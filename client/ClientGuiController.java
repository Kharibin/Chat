package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.MessageType;

public class ClientGuiController extends Client{
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);

    protected SocketThread getSocketThread(){
        return new GuiSocketThread();
    }

    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.run();
    }


    protected String getServerAddress(){
        return view.getServerAddress();
    }

    protected int getServerPort(){
        return view.getServerPort();
    }

    protected String getUserName(){
        return view.getUserName();
    }

    protected ClientGuiModel getModel(){
        return this.model;
    }

    public static void main(String[] args) {
        ClientGuiController clientGuiController = new ClientGuiController();
        clientGuiController.run();
    }



    public class GuiSocketThread extends SocketThread{
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
            super.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
