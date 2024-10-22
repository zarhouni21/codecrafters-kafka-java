import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandleClient extends Thread{
    Socket clientSocket ;

    public HandleClient(Socket socket){
        clientSocket = socket ;
    }
    @Override
    public void run() {
        try {
            InputStream rawRequest = clientSocket.getInputStream() ;
            byte[] length = rawRequest.readNBytes(4) ;
            byte[] apiKey = rawRequest.readNBytes(2);
            byte[] apiVersion = rawRequest.readNBytes(2);
            byte[] correlation_id = rawRequest.readNBytes(4) ;

            byte[] errorCode = new byte[] {0,0} ;

            System.out.println("the raw request contains : \nMessageLength :"+ Arrays.toString(length) +
                    ",\napiKey : "+Arrays.toString(apiKey)+
                    ",\napiVersion : "+Arrays.toString(apiVersion)+
                    ",\nCorrelationId : "+Arrays.toString(correlation_id));

            var arr = new ByteArrayOutputStream() ;
            arr.write(correlation_id);

            int apiVersionDecoded = 0 ;
            for (byte b : apiVersion){
                apiVersionDecoded = (apiVersionDecoded << 8 ) + (b & 0xFF) ;
            }
            System.out.println(" the api version is " + apiVersionDecoded);
            if(apiVersionDecoded<=-1 ||apiVersionDecoded>=5 ) {
                arr.write(new byte[]{0,35});
            }
            else{
                arr.write(new byte[]{0,0}); //error_code => INT16
                arr.write(2); // array size + 2
                arr.write(apiKey); //api_key => INT16
                arr.write(new byte[]{0,3}); // min_version => INT16
                arr.write(new byte[]{0,4}); // max_version => INT16
                arr.write(new byte[]{0}) ; // tagged_fields
                arr.write(new byte[]{0,0,0,0}); // throttle_time_ms => INT32
                arr.write(new byte[]{0}) ; // tagged_fields

            }
            int size = arr.size() ;
            byte[] respSize = ByteBuffer.allocate(4).putInt(size).array();
            byte[] res = arr.toByteArray() ;


            OutputStream out = clientSocket.getOutputStream() ;

            out.write(respSize);
            out.write(res);


            out.flush() ;
            rawRequest.close();
            System.out.println("end of processing");
        }
        catch(Exception e){
            System.out.println("Thread : IOException: " + e.getMessage());
        }
        // read from the client:

    }

}
