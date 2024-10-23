import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandleClient extends Thread{
    InputStream in ; 
    OutputStream out ;

    public HandleClient(InputStream in , OutputStream out){
        this.in = in ; 
        this.out = out ; 
    }
    @Override
    public void run() {
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

}
