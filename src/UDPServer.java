import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UDPServer {
    public static void main(String[] args) {
        try {
            DatagramSocket socketServeur = new DatagramSocket(null);
            InetSocketAddress adresse = new InetSocketAddress("localhost", 5000);
            socketServeur.bind(adresse);
            System.out.println("Serveur en attente sur le port 5000...");

            while (true) {
                byte[] recues = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);

                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                String clientIP = paquetRecu.getAddress().getHostAddress();
                int clientPort = paquetRecu.getPort();
                System.out.println("Nouveau client : " + clientIP + ":" + clientPort);
                System.out.println("Message reçu : " + message);

                String reponse = "Message reçu : " + message;
                byte[] envoyees = reponse.getBytes();
                DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, paquetRecu.getAddress(), clientPort);
                socketServeur.send(paquetEnvoye);
                System.out.println("Réponse envoyée.");

                while (true) {
                    paquetRecu = new DatagramPacket(recues, recues.length);
                    socketServeur.receive(paquetRecu);
                    String userMessage = new String(paquetRecu.getData(), 0, paquetRecu.getLength());

                    if (userMessage.equalsIgnoreCase("exit")) {
                        System.out.println("Client " + clientIP + ":" + clientPort + " déconnecté.");
                        break;
                    }

                    System.out.println("Message du client : " + userMessage);
                    paquetEnvoye = new DatagramPacket(paquetRecu.getData(), paquetRecu.getLength(), paquetRecu.getAddress(), clientPort);
                    socketServeur.send(paquetEnvoye);
                    System.out.println("Réponse envoyée.");
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
