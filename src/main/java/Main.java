
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
      InputStream rawRequest = null ;
      OutputStream rawResponse = null ;
      try {
         serverSocket = new ServerSocket(port);
         // Since the tester restarts your program quite often, setting SO_REUSEADDR
         // ensures that we don't run into 'Address already in use' errors
         serverSocket.setReuseAddress(true);
         // Wait for connection from client.

         clientSocket = serverSocket.accept();
         while(true){
             rawRequest = clientSocket.getInputStream() ;
             rawResponse = clientSocket.getOutputStream() ;
             handleRequest(rawRequest , rawResponse);
             rawRequest.close();
             rawResponse.close(); 
//
//             System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!NEW LOOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//             byte[] length = rawRequest.readNBytes(4) ;
//             byte[] apiKey = rawRequest.readNBytes(2);
//             byte[] apiVersion = rawRequest.readNBytes(2);
//             byte[] correlation_id = rawRequest.readNBytes(4) ;
//
//             System.out.println("the raw request contains : \nMessageLength :"+ Arrays.toString(length) +
//                     ",\napiKey : "+Arrays.toString(apiKey)+
//                     ",\napiVersion : "+Arrays.toString(apiVersion)+
//                     ",\nCorrelationId : "+Arrays.toString(correlation_id));
//
//             var arr = new ByteArrayOutputStream() ;
//             arr.write(correlation_id);
//
//             int apiVersionDecoded = 0 ;
//             for (byte b : apiVersion){
//                 apiVersionDecoded = (apiVersionDecoded << 8 ) + (b & 0xFF) ;
//             }
//             int cId = 0 ;
//             for (byte b : correlation_id){
//                 cId = (cId << 8 ) + (b & 0xFF) ;
//             }
//             System.out.println(" The C_ID version is " + cId);
//             if(apiVersionDecoded<=-1 ||apiVersionDecoded>=5 ) {
//                 arr.write(new byte[]{0,35});
//                 arr.write(2); // array size + 2
//                 arr.write(new byte[]{0,2}); //api_key => INT16
//                 arr.write(new byte[]{0,3}); // min_version => INT16
//                 arr.write(new byte[]{0,4}); // max_version => INT16
//                 arr.write(new byte[]{0}) ; // tagged_fields
//                 arr.write(new byte[]{0,0,0,0}); // throttle_time_ms => INT32
//                 arr.write(new byte[]{0}) ; // tagged_fields
//             }
//             else{
//                 arr.write(new byte[]{0,0}); //error_code => INT16
//                 arr.write(2); // array size + 2
//                 arr.write(apiKey); //api_key => INT16
//                 arr.write(new byte[]{0,3}); // min_version => INT16
//                 arr.write(new byte[]{0,4}); // max_version => INT16
//                 arr.write(new byte[]{0}) ; // tagged_fields
//                 arr.write(new byte[]{0,0,0,0}); // throttle_time_ms => INT32
//                 arr.write(new byte[]{0}) ; // tagged_fields
//
//             }
//             int size = arr.size() ;
//             byte[] respSize = ByteBuffer.allocate(4).putInt(size).array();
//             byte[] res = arr.toByteArray() ;
//
//             OutputStream out = clientSocket.getOutputStream() ;
//
//             out.write(respSize);
//             out.write(res);
//             out.flush() ;
             System.out.println("end of processing");
         }
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

  public static void  handleRequest(InputStream in , OutputStream out){
      try{
          DataInputStream dataIn = new DataInputStream(in) ;
          int regLength = dataIn.readInt() ; // return the first 4 bytes, which include the length
          ByteBuffer req = ByteBuffer.allocate(regLength).put(dataIn.readAllBytes()).rewind() ;
          short apiKey = req.getShort() ;
          short apiVersion = req.getShort() ;
          int correlationId = req.getInt() ;
          short error = 0  ;

          if(apiVersion<0 ||apiVersion>4){
              error = 35 ;
          }

          short API_KEY = 18 ;
          short MIN_VERSION = 3 ;
          short MAX_VERSION = 4 ;
          ByteBuffer buffer = ByteBuffer.allocate(1024).putInt(correlationId)
                  .putShort(error)
                  .put((byte) 2)
                  .putShort(API_KEY)
                  .putShort(MIN_VERSION)
                  .putShort(MAX_VERSION)
                  .put((byte) 0)
                  .putInt(0)
                  .put((byte) 0)
                  .flip() ;

          byte[] res = new byte[buffer.remaining()] ;
          buffer.get(res) ;

          out.write(res.length);
          out.write(res);
      }
      catch(Exception e){

      }



  }

}
