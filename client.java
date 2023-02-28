
// A Java program for a Client

import java.net.*;
import java.io.*;
import java.util.Scanner;

import static java.lang.System.exit;

public class client {
    public static void main(String args[]) {
        try {
            //Get the IP address
            InetAddress ip = InetAddress.getByName("127.0.0.1");

            //set port number for TCP connection
            int port = 3500;

            System.out.println("Establishing connection. Please wait...");

            Socket socket = new Socket(ip, port);
            System.out.println("Connected");

            // takes input from terminal
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // sends output to the socket
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            //Set to get input from user
            Scanner sc = new Scanner(System.in);

            System.out.println("What is your name ? ");
            String userName = sc.nextLine();
            dataOutputStream.writeUTF(userName);

            System.out.println("Hi " + userName + " you can start chatting with friends ... Type bye to exit\n");

            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String server_line = dataInputStream.readUTF();
                            System.out.println(server_line);
                            if(server_line.equalsIgnoreCase("Good Bye.")){
                                exit(0);
                            }
                        } catch (IOException e) {
                            System.out.println("Server Disconnected...");
                            System.exit(0);
                        }
                    }
                }
            });
            readMessage.start();

            while (true) {
                String message = sc.nextLine();
                dataOutputStream.writeUTF(userName + " :" + message);
            }

        } catch (IOException u) {
            System.out.println(u);
        }
    }
}
