package miage_TP1_FTP_socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(2121);
            System.out.println("Attente de connexion sur le port 2121 ...");
            Socket socket = serverSocket.accept();

            System.out.println("Client connect�.");

            InputStream in = socket.getInputStream();
            Scanner scanner = new Scanner(in);
            OutputStream out = socket.getOutputStream();
            
            out.write("220 Service ready\r\n".getBytes());
            
            String username = scanner.nextLine().trim();
            
            out.write("331 User name ok, need password".getBytes());
            username+="\r\n";
            out.write(username.getBytes());
            
            String password = scanner.nextLine().trim();
            out.write("331 Enter password\r\n".getBytes());
            password+="\r\n";
            out.write(password.getBytes());
            
            if (userAuth(username, password)) {
                out.write("230 User logged in\r\n".getBytes());
            } else {
                out.write("530 Login incorrect\r\n".getBytes());
                scanner.close();
                serverSocket.close();
                return;
            }
            
            String clientCommand;
            do {
                clientCommand = scanner.nextLine();
            
                switch (clientCommand.toLowerCase()) {
                    case "quit":
                        System.out.println("221 D�connexion.\r\n");
                        break;
                    case "get":
            
                        break;
                    case "dir":
            
                        break;
                    case "cd":

                        break;
                    default:
                        out.write("500 Commande non reconnue.\r\n".getBytes());
                }
            
            } while (!clientCommand.equalsIgnoreCase("quit"));
            
            scanner.close();
            in.close();
            out.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
    }


    private static boolean userAuth(String username, String password) {
        return username.equals("ezzahra") && password.equals("password");
    }
}