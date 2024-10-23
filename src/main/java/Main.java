
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    // 
     ServerSocket serverSocket = null;
     int port = 9092;
     try{
         serverSocket = new ServerSocket(port) ;
         serverSocket.setReuseAddress(true);
         handleConnection(serverSocket);
     } catch(Exception e){
         throw new RuntimeException( );
      }
     }



  public static void handleConnection(ServerSocket server){
      try(Socket client = server.accept()){
          System.out.println("new connection have been made.");
          InputStream in = client.getInputStream() ;
          DataOutputStream out = new DataOutputStream(client.getOutputStream() );
          List<HandleClient> handlers = new ArrayList<HandleClient>() ;
          while(in!=null){
              // create new Request Handler :
              HandleClient handler = new HandleClient(in , client.getOutputStream()) ;
              handlers.add(handler) ;
              handler.start();
              in.close();
//              AcceptRequest(in , client.getOutputStream()) ;
          }
      }
      catch(Exception e) {
          System.out.println("Accepting connection failed :( : \n\t"+e.getMessage());
      }
  }

  public static void AcceptRequest(InputStream rawRequest, OutputStream rawResponse) {
      try {
          System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!NEW LOOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          byte[] length = rawRequest.readNBytes(4);
          byte[] apiKey = rawRequest.readNBytes(2);
          byte[] apiVersion = rawRequest.readNBytes(2);
          byte[] correlation_id = rawRequest.readNBytes(4);

          System.out.println("the raw request contains : \nMessageLength :" + Arrays.toString(length) +
                  ",\napiKey : " + Arrays.toString(apiKey) +
                  ",\napiVersion : " + Arrays.toString(apiVersion) +
                  ",\nCorrelationId : " + Arrays.toString(correlation_id));

          var arr = new ByteArrayOutputStream();
          arr.write(correlation_id);

          int apiVersionDecoded = 0;
          for (byte b : apiVersion) {
              apiVersionDecoded = (apiVersionDecoded << 8) + (b & 0xFF);
          }
          int cId = 0;
          for (byte b : correlation_id) {
              cId = (cId << 8) + (b & 0xFF);
          }
          System.out.println(" The C_ID version is " + cId);
          if (apiVersionDecoded <= -1 || apiVersionDecoded >= 5) {
              arr.write(new byte[]{0, 35});
              arr.write(2); // array size + 2
              arr.write(new byte[]{0, 2}); //api_key => INT16
              arr.write(new byte[]{0, 3}); // min_version => INT16
              arr.write(new byte[]{0, 4}); // max_version => INT16
              arr.write(new byte[]{0}); // tagged_fields
              arr.write(new byte[]{0, 0, 0, 0}); // throttle_time_ms => INT32
              arr.write(new byte[]{0}); // tagged_fields
          }
          else {
              arr.write(new byte[]{0, 0}); //error_code => INT16
              arr.write(2); // array size + 2
              arr.write(apiKey); //api_key => INT16
              arr.write(new byte[]{0, 3}); // min_version => INT16
              arr.write(new byte[]{0, 4}); // max_version => INT16
              arr.write(new byte[]{0}); // tagged_fields
              arr.write(new byte[]{0, 0, 0, 0}); // throttle_time_ms => INT32
              arr.write(new byte[]{0}); // tagged_fields

          }
          int size = arr.size();
          byte[] respSize = ByteBuffer.allocate(4).putInt(size).array();
          byte[] res = arr.toByteArray();

          rawResponse.write(respSize);
          rawResponse.write(res);
          rawResponse.flush();
          System.out.println("end of processing");
      } catch (IOException e) {
          System.out.println("Request handler, IOException: " + e.getMessage());

      }
  }
  public static void  handleRequest(InputStream in , DataOutputStream out){
      System.out.println("============== NEW REQUEST!! ============");
      try{
          DataInputStream dataIn = new DataInputStream(in) ;
          int regLength = dataIn.readInt() ; // return the first 4 bytes, which include the length
          ByteBuffer req = ByteBuffer.allocate(regLength).put(dataIn.readNBytes(regLength)).rewind() ;
          short apiKey = req.getShort() ;
          short apiVersion = req.getShort() ;
          int correlationId = req.getInt() ;
          System.out.println("request data : \n"+"\t api key: "+apiKey+"\n\t api version: "+apiVersion+"\n\t correlation Identifier: "+correlationId+"\nend of request.");
          short error = 0  ;

          if(apiVersion<0 ||apiVersion>4){
              error = 35 ;
          }

          short API_KEY = 18 ;
          short MIN_VERSION = 3 ;
          short MAX_VERSION = 4 ;
          System.out.println("filling out the response. ");
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

          System.out.println("Sending out the response.");
          System.out.println("response's size : " + res.length);
          out.write(res.length);
          out.write(res);
          System.out.println("reponse was sent.");
      }
      catch(Exception e){
          System.out.println("Handler, IOException :" + e.getMessage());
      }



  }

    public static ByteBuffer processRequest(InputStream input) throws  IOException{
        int length = ByteBuffer.wrap(input.readNBytes(4)).getInt() ;
        System.out.println("the length of the request is : "+ length);
        var payload = input.readNBytes(length) ;
        return  ByteBuffer.allocate(length).put(payload).rewind();
    }

    private static ByteBuffer process(ByteBuffer request) {
        var apiKey = request.getShort();     // request_api_key
        var apiVersion = request.getShort(); // request_api_version
        var correlationId = request.getInt();

        short error = 0 ;
        if(apiVersion<0 ||apiVersion>4){
            error = 35 ;
        }

        short API_KEY = 18 ;
        short MIN_VERSION = 3 ;
        short MAX_VERSION = 4 ;
        System.out.println("filling out the response. ");
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

        return buffer ;
    }

    private static void respond(ByteBuffer response, OutputStream outputStream) throws IOException {
        outputStream.write(response.array());
    }

}
