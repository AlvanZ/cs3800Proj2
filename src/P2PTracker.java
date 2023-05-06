import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class P2PTracker {
    private final int port = 1234;
    private final DatagramSocket socket;
    DatagramPacket packet;

    private HashMap<String, InetAddress > clients = new HashMap<>();

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
            packet = new DatagramPacket(buffer, buffer.length);
    
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
                    processUsername();
                } catch (IOException e) {
                    // Handle exception
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void processUsername() throws IOException {
        String request = new String(packet.getData(), 0, packet.getLength());
        String fields[] = request.split(",");
    
        String tag = fields[0];
        String msg = fields[1];
        String time = fields[2];
    
        if (tag.equals("username")) {

            if (!clients.containsKey(msg)) {
                clients.put(msg, packet.getAddress());
                System.out.println("New user: " + msg);

                send(Utility.formmatPayload("username", msg, Utility.getCurrentTime()), packet.getAddress(), packet.getPort());
                send(Utility.formmatPayload("message", "@Server: " + msg + " has joined the chat!", Utility.getCurrentTime()), packet.getAddress(), packet.getPort());

                sendClientList();
  

            }
            else{

                send(Utility.formmatPayload("username", "@Server: Enter a different username", Utility.getCurrentTime()), packet.getAddress(), packet.getPort());
            }
           
        //   if (!userMap.containsKey(msg)) {
        //     username = msg;
        //     userMap.put(username, this);
    
        //     // Send confirmation of username to the client
        //     sendMessage(Utility.formmatPayload("username", msg, time));
        //     // Add a join message to the message queue
        //     msgList.addToQueue(
        //       LocalDateTime.now(),
        //       Utility.formmatPayload(
        //         "message",
        //         "@Server: " + username + " has joined the chat!",
        //         time
        //       ) +
        //       "," +
        //       username
        //     );
        //   } else {
        //     // Send error message for duplicate username
        //     sendMessage(
        //       Utility.formmatPayload(
        //         "username",
        //         "@Server: Enter a different username",
        //         Utility.getCurrentTime()
        //       )
        //     );
        //  }
        }
      }

    private void sendClientList() throws IOException {

        for (String key : clients.keySet()) {
            send(Utility.formmatPayload("add", key + clients.get(key).toString(), Utility.getCurrentTime()), packet.getAddress(), packet.getPort());
        }
    }

    

    public static void main(String[] args) throws SocketException, UnknownHostException {
        P2PTracker server = new P2PTracker();

        server.start();
        
    }
}
