
import Server.Server;

import java.io.*;

public class Main {

  public static void main(String[] args){
    System.err.println("Logs from your program will appear here!");

      try{
          Server server = new Server() ;
          server.run();
      } catch (IOException e){
          System.out.println("Main, couldn't start the server : "+e.toString());
      }

     }
}
