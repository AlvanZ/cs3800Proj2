import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

  // public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String username;

  private Socket socket;
  private List msgList;
  private Map userMap;
  private String request;

  
  public ClientHandler (Socket socket, List msgList,  Map userName){
    try{
      this.socket = socket;
      this.msgList = msgList;
      this.userMap = userName;

      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      sendMessage(payload("username", "@Server: Enter your username" , "a"));
          
    } catch(IOException e) {
      close();
    }
  }

  public void setUsername(String name){
    username = name;
  }


  public void sendMessage(String message){
      try{
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }catch(IOException e){
        close();
      }   
  }

  public void listenToMessage() {
    String msgFromClient = ""; 
    try {
        while (socket.isConnected()) {
            if (bufferedReader.ready()) {

                msgFromClient = bufferedReader.readLine();
                System.out.println(msgFromClient);

                if (username != null){
                  msgList.add(msgFromClient + "," + username);
                }
                else{
                  processUsername(msgFromClient); 
                }
            }
        }
    } catch (IOException e) {
        close();
    }
  }

  public void processUsername(String request){
    String fields[] = request.split(",");

    String tag = fields[0];
    String msg = fields[1];

    if(tag.equals("username")){
      
      if(!userMap.containsKey(msg)){
        username = msg;
        userMap.put(username, this);


        sendMessage(payload("username", msg , "a"));
        msgList.add(payload("message", "@Server: " + username + " has join the chat!", "a") +  "," + username);

      }
      else{
        sendMessage(payload("username", "@Server: Enter a different username" , "a"));
      }

    }

  }

  


public void close(){
  try {
      
      if(bufferedReader != null) {
          bufferedReader.close();
      }
      if(bufferedWriter != null) {
          bufferedWriter.close();
      }
      if(socket != null) {
          socket.close();
      }
  }
  catch(IOException e) {
      e.printStackTrace();
  }
}

  public String payload(String tag, String msg,  String time ){
    String []response = {tag,msg,time};
    return String.join(",", response);

  }


  @Override
  public void run() {
    listenToMessage();
  }

  
}
