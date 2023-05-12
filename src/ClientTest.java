import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ClientTest extends Application {

  private static int numWindows = 10;
  private final String[] names = { "Bob", "Derek", "Regan" };

  private static ArrayList<P2PClient> uiClients = new ArrayList<>();

  @Override
  public void start(Stage primaryStage) throws Exception {
    for (int i = 0; i < numWindows; i++) {
      final int index = i;
      Platform.runLater(() -> {
        P2PClient ui = new P2PClient(Integer.toString(index));
        uiClients.add(ui);
        try {
          ui.start(new Stage());
          ui.demoName();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    // Start a separate thread to get input from command line
    Thread inputThread = new Thread(() -> {
      Scanner scanner = new Scanner(System.in);
      System.out.print("Demo with messages (demo)? ");
      String demo = scanner.nextLine();
      if (demo.equals("demo")) {
        for (int i = 0; i < numWindows; i++) {
          uiClients.get(i).demoMessages();
        }
      }
    });
    inputThread.start();
  }

  // Add path to VM args
  public static void main(String[] args) {
    launch(args);
  }
}
