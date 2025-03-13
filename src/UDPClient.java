import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    private static final int SERVER_PORT = 5432;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine();
            sendMessage(clientSocket, serverAddress, username);

            new Thread(() -> receiveMessages(clientSocket)).start();

            while (true) {
                System.out.print("Message (format: destinataire:message ou 'exit' pour quitter) : ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    sendMessage(clientSocket, serverAddress, "exit");
                    System.out.println("Déconnexion...");
                    break;
                }

                sendMessage(clientSocket, serverAddress, message);
            }

            clientSocket.close();
            scanner.close();
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, String message) {
        try {
            byte[] envoyees = message.getBytes();
            DatagramPacket packet = new DatagramPacket(envoyees, envoyees.length, address, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("Erreur envoi message : " + e.getMessage());
        }
    }

    private static void receiveMessages(DatagramSocket socket) {
        try {
            while (true) {
                byte[] recues = new byte[1024];
                DatagramPacket packet = new DatagramPacket(recues, recues.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("\n[Message reçu] " + message);
                System.out.print("Message (format: destinataire:message ou 'exit' pour quitter) : ");
            }
        } catch (Exception e) {
            System.err.println("Erreur réception message : " + e.getMessage());
        }
    }
}