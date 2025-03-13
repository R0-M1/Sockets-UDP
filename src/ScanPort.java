import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class ScanPort {

    public static void main(String[] args) {
        int startPort = 100;
        int endPort = 1000;
        scanPorts(startPort, endPort);
    }

    public static void scanPorts(int startPort, int endPort) {
        HashMap<Integer, String> etatPorts = new HashMap<>();
        for (int port = startPort; port <= endPort; port++) {
            if (isPortOpen(port)) {
                etatPorts.put(port, "ouvert");
            } else {
                etatPorts.put(port, "fermé");
            }
        }
        System.out.println(etatPorts);
    }

    private static boolean isPortOpen(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            return false;
        } catch (SocketException e) {
            return true;
        }
    }
}
