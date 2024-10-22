
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    // 
     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     int port = 9092;
     try {
       serverSocket = new ServerSocket(port);
       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);
       // Wait for connection from client.
       clientSocket = serverSocket.accept();


       // read from the client:
       InputStream rawRequest = clientSocket.getInputStream() ;
       byte[] length = rawRequest.readNBytes(4) ;
       byte[] apiKey = rawRequest.readNBytes(2);
       byte[] apiVersion = rawRequest.readNBytes(2);
       byte[] correlation_id = rawRequest.readNBytes(4) ;

       byte[] errorCode = new byte[] {0,0} ;

       System.out.println("the raw request contains : \nMessageLength :"+Arrays.toString(length) +
               ",\napiKey : "+Arrays.toString(apiKey)+
               ",\napiVersion : "+Arrays.toString(apiVersion)+
               ",\nCorrelationId : "+Arrays.toString(correlation_id));

       int apiVersionDecoded = 0 ;
       for (byte b : apiVersion){
           apiVersionDecoded = (apiVersionDecoded << 8 ) + (b & 0xFF) ;
       }
       System.out.println(" the api version is " + apiVersionDecoded);
       if(apiVersionDecoded<=-1 ||apiVersionDecoded>=5 ) {
           errorCode[0] = (byte) (35 >> 8) ;
           errorCode[1] = (byte) (35) ;
       }
       System.out.println("errorCode is:"+Arrays.toString(errorCode));

       System.out.println("calculating the message length: ");

       int messageLength = 0 ;
       for(byte b : correlation_id){messageLength += b ; }
       for(byte b : errorCode) { messageLength += b ; }
       for(byte b : apiVersion) { messageLength +=b ;}

       System.out.println(" the sum of response is :"+messageLength);

       byte[] responseLength = new byte[] {0,0,0,0} ;
       responseLength[0] = (byte) (messageLength >> 32) ;
       responseLength[1] = (byte) (messageLength >> 16) ;
       responseLength[2] = (byte) (messageLength >> 8) ;
       responseLength[3] = (byte) (messageLength) ;

       OutputStream out = clientSocket.getOutputStream() ;
       System.out.println("writing to client");
       out.write(responseLength);
       out.write(correlation_id);
       out.write(errorCode);
       out.write(apiVersion);

       out.close() ;
       rawRequest.close();

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     } finally {
       try {
         if (clientSocket != null) {
           clientSocket.close();

         }
       } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
       }
     }
  }
}
