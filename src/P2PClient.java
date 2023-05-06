import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class P2PClient extends Application {

  private TextArea chatArea;
  private ArrayList<String> chatMessages = new ArrayList<>();
  private TextField messageField;
  private Button sendButton;

  private String userName;
  private String promptText = "Enter your message here...";

  private String demoName;
  private Boolean demo = false;

  public static final Integer PORT_NUMBER = 1234;
  public static final String HOST_NAME = "localhost";

  private DatagramSocket socket;
  private DatagramPacket packet;



  public P2PClient(String name) {
    demoName = name;
  }

  public P2PClient() {}

  public void demoName() {
    //sendMessage(demoName, "");
  }

  public void demoMessages() {
    demo = true;
    for (int i = 1; i <= 5; i++) {
      //sendMessage(Integer.toString(i), "tag");
    }
  }

  //DEMO

  @Override
  public void start(Stage primaryStage) {
    try {
      // Create the UI components
      BorderPane root = new BorderPane();
      chatArea = new TextArea();
      chatArea.setEditable(false);
      chatArea.setFocusTraversable(false);
      chatArea.getStyleClass().add("chat-area");

      messageField = new TextField();
      messageField.getStyleClass().add("message-field");
      messageField.setPromptText(promptText);

      // Create send Button
      sendButton = new Button("Send");
      sendButton.getStyleClass().add("send-button");

      // Create the vertical layout for the chat box + Horizontal box
      VBox chatBox = new VBox(chatArea, new HBox(messageField, sendButton));
      chatBox.getStyleClass().add("chat-box");

      // Set margin for the send button
      HBox.setMargin(sendButton, new Insets(0, 0, 0, 10));

      // Add the chat box to the root
      root.getStyleClass().add("root");
      root.setCenter(chatBox);

      // Create the Scene and set it as the content of the primary Stage
      Scene scene = new Scene(root, 535, 250);
      scene
        .getStylesheets()
        .add(getClass().getResource("application.css").toExternalForm());

      // Set the stage
      primaryStage.setScene(scene);
      primaryStage.setTitle("Client");
      primaryStage.show();

      // Focus to root
      root.requestFocus();

      // Start the socket
      initializeClient(HOST_NAME, PORT_NUMBER);
      startClient();

      // Event for chatArea
      chatArea
        .textProperty()
        .addListener((observable, oldText, newText) -> {
          if (userName != null) {
            String title = userName.replace("[", "").replace("]", "");
            primaryStage.setTitle(title);
          }
        });

      //Even when the message field is clicked: DEMO
      messageField.setOnMouseClicked(event -> {
        demo = false;
      });

      // Event handler for messageField to handle the Enter key
      messageField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ENTER) {
          //DEMO
          demo = false;

          //sendMessage(messageField.getText(), "tag");
          Platform.runLater(() -> {
            messageField.clear();
          });
        }
      });

      // Event handler for sendButton
      sendButton.setOnAction(event -> {
        //DEMO
        demo = false;

       // sendMessage(messageField.getText(), "tag");
        Platform.runLater(() -> {
          messageField.clear();
        });
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateChatBox(String message) {
    // Update chat box
    Platform.runLater(() -> {
      chatArea.appendText(message + "\n");
    });
  }

  // Disable button and
  public void signOff() {
    messageField.setDisable(true);
    sendButton.setDisable(true);
  }

  public TextField getMessageField() {
    return messageField;
  }

  public Button getSendButton() {
    return sendButton;
  }

  //NOTE: Socket

  private void initializeClient(String host, Integer port) {
    try {
      this.socket = new DatagramSocket(port);
      
    } catch (IOException e) {
      System.out.println("Unable to connect to server at " + host + ":" + port);
      updateChatBox("Unable to connect to server!");
      e.printStackTrace();
      close();
    }
  }

//   public synchronized void sendMessage(String message, String tag) {
//     String response;
//     try {
//       if (socket.isConnected()) {
//         if (message.length() > 0) {
//           if (message.equals(".")) {
//             tag = "disconnect";
//           } else {
//             tag = (userName == null) ? "username" : "message";

//             message = (userName == null) ? "[" + message + "]" : message;
//           }

//           response =
//             Utility.formmatPayload(tag, message, Utility.getCurrentTime());

//             // TODO: add datagram send
//         //   bufferedWriter.write(response);
//         //   bufferedWriter.newLine();
//         //   bufferedWriter.flush();
//         }
//       }
//  } //catch (IOException e) {
//     //   close();
//     // }
//   }

//   public void listenToMessage(TextArea screen) {
//     String msgFromGroupChat = "";
//     try {
//       while (socket.isConnected()) {
//         if (bufferedReader.ready()) {
//           msgFromGroupChat = bufferedReader.readLine();
//           processResponse(msgFromGroupChat);
//         }
//       }
//     } catch (IOException e) {
//       close();
//     }
//   }

  private void processResponse(String response) {
    System.out.println("Response: " + response);

    String fields[] = response.split(",");

    String tag = fields[0];
    String msg = fields[1];
    String rawTime = fields[2];

    String time = demo
      ? Utility.demoFormatTime(Utility.stringToLocalDateTime(rawTime))
      : Utility.formatTime(Utility.stringToLocalDateTime(rawTime));

    if (tag.equals("disconnect")) {
      updateChatBox(time + msg);
      signOff();
      //close();
    } else if (tag.equals("username")) {
      if (!msg.contains("@Server")) {
        userName = msg;
      } else {
        updateChatBox(time + msg);
      }
    } else {
      updateChatBox(time + msg);
    }
  }

  public void startClient() {
    Thread listenToMessageThread = new Thread(
      new Runnable() {
        @Override
        public void run() {
          //listenToMessage(chatArea);
        }
      }
    );
    listenToMessageThread.start();
  }

//   public void close() {
//     try {
//       if (bufferedReader != null) {
//         bufferedReader.close();
//       }
//       if (bufferedWriter != null) {
//         bufferedWriter.close();
//       }
//       if (socket != null) {
//         socket.close();
//       }
//     } catch (IOException e) {
//       e.printStackTrace();
//     }
//   }

  public static void main(String[] args) {
    launch(args);
  }
}
