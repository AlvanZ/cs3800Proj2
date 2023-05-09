import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.HashMap;

public class P2PTracker {
    private final int port = 1234;
    private final DatagramSocket socket;
   

    private HashMap<String, String[] > clients = new HashMap<>();

    public P2PTracker() throws SocketException, UnknownHostException {;
        this.socket = new DatagramSocket(port);
        System.out.println("Start tracker on port " + port + " at " + InetAddress.getLocalHost());
    }

    public void start() {
        Thread receiveThread = new Thread(new ReceiveThread());
        receiveThread.start();
    }

   

    private class ReceiveThread implements Runnable {
        
        private int port_client;
        private DatagramPacket packet;
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
                    port_client = packet.getPort();
    
                    // Process the received message here
                    System.out.println("Received message from " + address.getHostAddress() + ":" + port_client + ": " + message);
                    processUsername();
                } catch (IOException e) {
                    // Handle exception
                    e.printStackTrace();
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
                    sendClientList();
                    String name = msg;
                    clients.put(name, new String[]{packet.getAddress().toString(),Integer.toString(port_client)});
                    
                    System.out.println("New user: " + name + "port: " + clients.get(name)[1]);
    
                    send(Utility.formmatPayload("username", name, Utility.getCurrentTime()), packet.getAddress(), packet.getPort());

                    String current_time = Utility.getCurrentTime();
                

                    broadCast(Utility.formmatPayload("message", "@Server: " + name+ " has joined the chat!", current_time));
                    //send(Utility.formmatPayload("message", "@Server: " + name+ " has joined the chat!", current_time), packet.getAddress(), packet.getPort());

                    addClient(name, packet.getAddress().toString(), Integer.toString(port_client), time);
    
      
    
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
    
          public void send(String message, InetAddress address, int port) throws IOException {
    
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        }
        private void sendClientList() throws IOException {
    
            for (String key : clients.keySet()) {
                send(Utility.formmatPayload("add", key + "#" + clients.get(key)[0].toString() + "#" + clients.get(key)[1].toString(), Utility.getCurrentTime()), packet.getAddress(), packet.getPort());
            }
        }

        private void addClient(String name, String ip, String port, String time) throws IOException {
    
            for (String key : clients.keySet()) {

                String[] data = clients.get(key);
                // TODO: remove the / before ip
                data[0] = data[0].replace('/',' ' ).strip();

                InetAddress revc_ip = InetAddress.getByName(data[0]);
                Integer p = Integer.parseInt(data[1]);
                send(Utility.formmatPayload("add", name + "#" + ip + "#" + port, time), revc_ip, p);
            }
        }

        private void broadCast(String msg) throws IOException {
    
            for (String key : clients.keySet()) {

                String[] data = clients.get(key);
                // TODO: remove the / before ip
                data[0] = data[0].replace('/',' ' ).strip();

                InetAddress revc_ip = InetAddress.getByName(data[0]);
                Integer p = Integer.parseInt(data[1]);

                send(msg, revc_ip, p);
            }
        }


    }

   

    

    public static void main(String[] args) throws SocketException, UnknownHostException {
        P2PTracker server = new P2PTracker();

        server.start();
        
    }
}
