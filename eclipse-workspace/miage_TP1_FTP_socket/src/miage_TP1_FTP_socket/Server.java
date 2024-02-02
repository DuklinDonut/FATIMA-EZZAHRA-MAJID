package miage_TP1_FTP_socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String ROOT = "/";
    private static String currentDIR = "C:\\Users\\FATIMA EZZAHRA MAJID\\Downloads";
    private static DataOutputStream controlOutWriter;
    private BufferedReader controlIn;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(2121)) {
            System.out.println("Waiting for connection on port 2121...");
            Socket socket = serverSocket.accept();

            System.out.println("Client connected.");

            InputStream in = socket.getInputStream();
            Scanner scanner = new Scanner(in);
            OutputStream out = socket.getOutputStream();

            String username = "";
            int attempts = 0;

            while (attempts < 3) {
                out.write("220 Service ready, enter username:\r\n".getBytes());
                String command = scanner.nextLine().trim();
                System.out.println("Received command: " + command);

                if (!command.startsWith("USER")) {
                    out.write("503 Bad sequence of commands. Send USER command first.\r\n".getBytes());
                    continue;
                }

                String[] parts = command.split("\\s+");
                if (parts.length != 2) {
                    out.write("501 Syntax error in parameters or arguments.\r\n".getBytes());
                    continue;
                }

                username = parts[1];
                if (verifyUsername(username)) {
                    out.write("331 User name okay, need password.\r\n".getBytes());
                    String password = scanner.nextLine().trim().substring(5);
                    if (verifyPassword(password)) {
                        out.write("230 User logged in, proceed.\r\n".getBytes());
                        break;
                    } else {
                        out.write("530 Not logged in. Password incorrect.\r\n".getBytes());
                        System.out.println("Incorrect password. Closing connection.");
                        return;
                    }
                } else {
                    out.write("530 Not logged in. Username incorrect.\r\n".getBytes());
                    attempts++;
                }
            }

            while (true) {
                String ftpCommand = "";
                ftpCommand = scanner.nextLine().trim();
                System.out.println("Received FTP command: " + ftpCommand);
                out.write(("200 OK:  \r\n").getBytes());

                String[] commandParts = ftpCommand.split("\\s+");
                String ftpCommandPart = commandParts[0].toLowerCase();

                switch (ftpCommandPart) {
                    case "quit":
                        out.write("221 Déconnexion.\r\n".getBytes());
                        out.flush();
                        socket.close();
                        return;

					/*
					 * case "get": if (commandParts.length > 1) { String filePath =
					 * commandParts[1].trim(); System.out.println("File path: " + filePath);
					 * sendFileContents(filePath, out); } else {
					 * out.write("501 File path not specified\r\n".getBytes()); } break;
					 */

                    case "pwd":
                        System.out.println("in pwd");
                        pwd();
                        break;

                    case "cd":
                        System.out.println("in cd");
                        String dir = ftpCommand.substring(3).trim();
                        System.out.println(ftpCommand);
                        cd(dir);
                        break;

                    case "cwd":
                        String dir1 = ftpCommand.substring(4).trim();
                        cd(dir1);
                        break;

                    default:
                        out.write("500 Commande non reconnue.\r\n".getBytes());
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e);
            e.printStackTrace();
        }
    }

    private static boolean verifyUsername(String username) {
        System.out.println("Verifying username: " + username);
        return username.equals("EZZAHRA");
    }

    private static boolean verifyPassword(String password) {
        System.out.println("Verifying password: " + password);
        return password.equals("PASSWORD");
    }

    private static void cd(String dir) {
        if (dir.equals(ROOT)) {
            currentDIR = ROOT;
            System.out.println("250 Action sur le fichier exécutée avec succès.");
            return;
        }

        String newDirPath;
        if (dir.equals("..")) {
            int ind = currentDIR.lastIndexOf(FILE_SEPARATOR);
            //System.out.println(ind);
            if (ind > 0) {
                currentDIR = currentDIR.substring(0, ind);
            } else {
                System.out.println("Requête non exécutée : Vous êtes déjà dans le répertoire racine.");
                return;
            }
        } else {
            newDirPath = currentDIR + FILE_SEPARATOR + dir;
            File newDir = new File(newDirPath);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDIR = newDirPath;
                System.out.println("250 Action sur le fichier exécutée avec succès.");
            } else {
                System.out.println("Requête non exécutée : Fichier indisponible (ex., fichier introuvable, pas d'accès).");
            }
        }
        pwd();
    }

    private static void pwd() {
        String response = "257 \"" + currentDIR + FILE_SEPARATOR + "\" is the current directory.\r\n";
        System.out.println(response);
    }

    private static void printMsg(String msg) {
        try {
            controlOutWriter.writeBytes(msg+"\r\n");
            controlOutWriter.flush();
        } catch (IOException e) {
            System.out.println("error while printing message");
        }
    }

    /*private static void sendFileContents(String filePath, OutputStream outData) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                outData.write("550 File not found\r\n".getBytes());
                return;
            }

            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    outData.write(line.getBytes());
                    outData.write("\r\n".getBytes());
                }
                outData.write("226 File transfer successful\r\n".getBytes());
            }
        } catch (IOException e) {
            System.out.println("Error sending file contents: " + e.getMessage());
            try {
                outData.write("451 Error while processing file\r\n".getBytes());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }*/
}
