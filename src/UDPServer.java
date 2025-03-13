import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Objects;

public class UDPServer {
    private static final int PORT = 5432;
    private static HashMap<String, InetSocketAddress> clients = new HashMap<>();

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Serveur RX302 en attente sur le port " + PORT + "...");

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(paquetRecu);

                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                InetSocketAddress clientAddress = new InetSocketAddress(paquetRecu.getAddress(), paquetRecu.getPort());

                handleMessage(serverSocket, message, clientAddress);
            }
        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    private static void handleMessage(DatagramSocket socket, String message, InetSocketAddress clientAddress) {
        String[] parts = message.split(":", 2);

        if (parts.length == 1) {
            String clientName = parts[0];

            if (clientName.equalsIgnoreCase("exit")) {
                removeClient(clientAddress);
                return;
            } else if (clientName.equalsIgnoreCase("list")) {
                System.out.println("Envoie de la liste des clients à " + clientName + " (" + clientAddress + ")");
                sendMessage(socket, clients.keySet().toString(), clientAddress);
                return;
            } else if (clientName.startsWith("+")) {
                clients.put(clientName.replace("+",""), clientAddress);
                System.out.println("Nouveau client connecté : " + clientName + " (" + clientAddress + ")");
                sendMessage(socket, "Bienvenue " + clientName + "! Utilisez format: destinataire:message", clientAddress);
            }

        } else if (parts.length == 2) {
            String destinataire = parts[0];
            String contenu = parts[1];

            if (contenu.equalsIgnoreCase("exit")) {
                removeClient(clientAddress);
                return;
            } else if (destinataire.equals("*")) {

                for (InetSocketAddress address : clients.values()) {
                    if (!address.equals(clientAddress)) {
                        sendMessage(socket, contenu, address);
                        System.out.println("Broadcast envoyé à " + address + " : " + contenu);
                    }
                }
                return;
            } else if (clients.containsKey(destinataire)) {
                sendMessage(socket, contenu, clients.get(destinataire));
                System.out.println("Message envoyé à " + destinataire + " : " + contenu);
            } else {
                sendMessage(socket, "Utilisateur non trouvé : " + destinataire, clientAddress);
            }
        }
    }

    private static void sendMessage(DatagramSocket socket, String message, InetSocketAddress address) {
        try {
            byte[] data = message.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(data, data.length, address.getAddress(), address.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi au client : " + e.getMessage());
        }
    }

    private static void removeClient(InetSocketAddress address) {
        clients.values().removeIf(client -> client.equals(address));
        System.out.println("Client déconnecté : " + address);
    }
}