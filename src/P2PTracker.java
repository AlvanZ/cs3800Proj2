import java.io.IOException;
import java.net.*;

public class P2PTracker {
    private final int port = 1234;
    private final DatagramSocket socket;

    public P2PTracker() throws SocketException, UnknownHostException {;
        this.socket = new DatagramSocket(port);
        System.out.println("Start tracker on port " + port + " at " + InetAddress.getLocalHost());
    }

    public void start() {
        Thread receiveThread = new Thread(new ReceiveThread());
        receiveThread.start();
    }

    public void send(String message, InetAddress address, int port) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    
            System.out.println("Starting to receive messages....");
    
            while (true) {
                try {
                    // Wait for incoming packet
                    socket.receive(packet);
    
                    // Extract message, address and port of sender
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
    
                    // Process the received message here
                    System.out.println("Received message from " + address.getHostAddress() + ":" + port + ": " + message);
                } catch (IOException e) {
                    // Handle exception
                    e.printStackTrace();
                }
            }
        }
    }
    

    public static void main(String[] args) throws SocketException, UnknownHostException {
        P2PTracker server = new P2PTracker();

        server.start();
        
    }
}
