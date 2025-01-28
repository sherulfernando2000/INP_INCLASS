package lk.ijse;

import java.io.*;
import java.net.Socket;

public class ClientMaker extends Thread {

   private Socket socket;
   private BufferedReader bufferedReader;
   private BufferedWriter bufferedWriter;
   private String userName;
   private Server server;


    public ClientMaker(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            userName = bufferedReader.readLine();
            this.server.broadcastMessage(this, userName+"/#sendingClientName#/"+ "HELLO I JOINED ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void run() {
        while (true){
            try {
                String message = bufferedReader.readLine();
                if (message != null) {
                    server.broadcastMessage(this, message);
                }
            } catch (IOException e) {
                server.broadcastMessage(this, userName+"/#sendingClientName#/"+ "left the chat");
                server.removeClient(this);
                break;

            }

        }
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}