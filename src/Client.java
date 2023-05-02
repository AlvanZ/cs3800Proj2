import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client  {

  private ArrayList<String> chatMessages = new ArrayList<>();

  public Client(){

  }


  //NOTE: Socket

  // private void initializeClient(String host, Integer port) {
  //   try {
  //     this.socket = new Socket(host, port);
  //     this.bufferedWriter =
  //       new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  //     this.bufferedReader =
  //       new BufferedReader(new InputStreamReader(socket.getInputStream()));
  //   } catch (IOException e) {
  //     System.out.println("Unable to connect to server at " + host + ":" + port);
  //     updateChatBox("Unable to connect to server!");
  //     e.printStackTrace();
  //     close();
  //   }
  // }



  // public synchronized void sendMessage(String message, String tag) {
  //   String response;
  //   try {
  //     if (socket.isConnected()) {
  //       if (message.length() > 0) {
  //         if (message.equals(".")) {
  //           tag = "disconnect";
  //         } else {
  //           tag = (userName == null) ? "username" : "message";

  //           message = (userName == null) ? "[" + message + "]" : message;
  //         }

  //         response =
  //           Utility.formmatPayload(tag, message, Utility.getCurrentTime());

  //         bufferedWriter.write(response);
  //         bufferedWriter.newLine();
  //         bufferedWriter.flush();
  //       }
  //     }
  //   } catch (IOException e) {
  //     close();
  //   }
  // }

  // public void listenToMessage(TextArea screen) {
  //   String msgFromGroupChat = "";
  //   try {
  //     while (socket.isConnected()) {
  //       if (bufferedReader.ready()) {
  //         msgFromGroupChat = bufferedReader.readLine();
  //         processResponse(msgFromGroupChat);
  //       }
  //     }
  //   } catch (IOException e) {
  //     close();
  //   }
  // }

  // private void processResponse(String response) {
  //   System.out.println("Response: " + response);

  //   String fields[] = response.split(",");

  //   String tag = fields[0];
  //   String msg = fields[1];
  //   String rawTime = fields[2];

  //   String time = demo
  //     ? Utility.demoFormatTime(Utility.stringToLocalDateTime(rawTime))
  //     : Utility.formatTime(Utility.stringToLocalDateTime(rawTime));

  //   if (tag.equals("disconnect")) {
  //     updateChatBox(time + msg);
  //     signOff();
  //     close();
  //   } else if (tag.equals("username")) {
  //     if (!msg.contains("@Server")) {
  //       userName = msg;
  //     } else {
  //       updateChatBox(time + msg);
  //     }
  //   } else {
  //     updateChatBox(time + msg);
  //   }
  // }

  // public void startClient() {
  //   Thread listenToMessageThread = new Thread(
  //     new Runnable() {
  //       @Override
  //       public void run() {
  //         listenToMessage(chatArea);
  //       }
  //     }
  //   );
  //   listenToMessageThread.start();
  // }

  // public void close() {
  //   try {
  //     if (bufferedReader != null) {
  //       bufferedReader.close();
  //     }
  //     if (bufferedWriter != null) {
  //       bufferedWriter.close();
  //     }
  //     if (socket != null) {
  //       socket.close();
  //     }
  //   } catch (IOException e) {
  //     e.printStackTrace();
  //   }
  // }

  public static void main(String[] args) {
    // Start the socket


    Scanner reader = new Scanner(System.in);

    System.out.println("Enter a username");
    String username = reader.nextLine();

    try {
      // Create the server object
      ServerSocket serverSocket = new ServerSocket(0);
      Server server = new Server(serverSocket);

      // Create a new thread to run the startServer() method
      Thread serverThread = new Thread(() -> {
        server.startServer();
      });

      // Start the server thread
      serverThread.start();

      

      // Create a new thread to run the processResponse() method
      Thread processThread = new Thread(() -> {
        while (!serverSocket.isClosed()) {
          server.processResponse();
        }
      });

        // Start the process thread
        processThread.start();


        System.out.println("Enter a message");
        sendMessage(reader.nextLine());

      } catch (IOException e) {
        // Exception handling for IO errors
        e.printStackTrace();
      }
    }
    // initializeClient(HOST_NAME, PORT_NUMBER);
    // startClient();

    public static void sendMessage(String message) {
 
    }
  }

