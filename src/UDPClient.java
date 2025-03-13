import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        try {
            DatagramSocket socketClient = new DatagramSocket();
            InetAddress adresseServeur = InetAddress.getByName("localhost");
            byte[] envoyees;
            byte[] recues = new byte[1024];

            String message = "hello serveur RX302";
            envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseServeur, 5000);
            socketClient.send(messageEnvoye);
            System.out.println("Message envoyé au serveur : " + message);

            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Réponse du serveur : " + reponse + " : " + paquetRecu.getAddress().getHostAddress() + ":" + paquetRecu.getPort());

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Entrez un message à envoyer (ou 'exit' pour quitter) : ");
                String userMessage = scanner.nextLine();

                if (userMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Fermeture du client...");
                    break;
                }

                envoyees = userMessage.getBytes();
                messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseServeur, 5000);
                socketClient.send(messageEnvoye);

                paquetRecu = new DatagramPacket(recues, recues.length);
                socketClient.receive(paquetRecu);
                reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Réponse du serveur : " + reponse);
            }

            socketClient.close();
            scanner.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
