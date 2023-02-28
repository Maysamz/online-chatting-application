import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.Vector;

public class server {
    public static Vector<functionForClientHandler> clients = new Vector<>();
    //Set to get input from user
    public Scanner sc = new Scanner(System.in);

    public void process() throws Exception {
        try {
            //Get the IP address
            InetAddress ip = InetAddress.getByName("127.0.0.1");

            //set port number
            int port = 3500;

            //declare a new ServerSocket on port 3500
            ServerSocket serverSocket = new ServerSocket(port);
            Socket newSocket;

            functionForsendMessage functionForsendMessage = new functionForsendMessage();
            Thread newThread1 = new Thread(functionForsendMessage);
            newThread1.start();

            while (true) {

                newSocket = serverSocket.accept();
                System.out.println("\nNew Client Connected from: " + newSocket);
                functionForClientHandler storeClientIntoVector = new functionForClientHandler(newSocket);
                Thread newThread = new Thread(storeClientIntoVector);
                clients.add(storeClientIntoVector);
                newThread.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        new server().process();
    }

    class functionForsendMessage extends Thread {
        public void run() {
            try {
                while (true) {
                    String client_line = sc.nextLine();

                    // send message to all connected users
                    for (functionForClientHandler client : clients) {
                        client.dataOutputStream.writeUTF("Server: " + client_line);
                    }
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    static class functionForClientHandler extends Thread {
        Socket newSocket;
        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;
        String clientName;

        public functionForClientHandler(Socket newSocket) throws Exception {
            this.newSocket = newSocket;
            this.dataInputStream = new DataInputStream(newSocket.getInputStream());
            this.dataOutputStream = new DataOutputStream(newSocket.getOutputStream());
        }

        public void run() {
            try {
                clientName = dataInputStream.readUTF();
                System.out.println(":" + clientName + ":" + " Has joined the chat");

                // send message to all connected users
                for (functionForClientHandler client : clients) {
                    if (client.dataOutputStream != dataOutputStream) {
                        client.dataOutputStream.writeUTF(":" + clientName + ":" + " Has joined the chat");
                    }
                }

                while (true) {
                    String client_line = dataInputStream.readUTF();
                    System.out.println(client_line);

                    if (client_line.contains("bye")) {
                        dataOutputStream.writeUTF("Good Bye.");
                        System.out.println(":" + clientName + ":" + " Has left the chat");
                        for (functionForClientHandler client : clients) {
                            if (client.dataOutputStream != dataOutputStream) {
                                client.dataOutputStream.writeUTF(":" + clientName + ":" + " Has left the chat");
                            }
                        }
                        break;
                    }

                    // send message to all connected users
                    for (functionForClientHandler client : clients) {
                        if (client.dataOutputStream != dataOutputStream) {
                            client.dataOutputStream.writeUTF(client_line);
                        }
                    }
                }

            } catch (IOException ex) {
                try {
                    dataOutputStream.writeUTF("Good Bye.");
                } catch (IOException e) {
                }
            }
        }
    }
}