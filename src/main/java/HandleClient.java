import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandleClient extends Thread{
    InputStream in ; 
    OutputStream out ;
    byte[] apiVersion  ;
    byte[] correlation_id ;
    byte[] apiKey ;


    public HandleClient(InputStream in , OutputStream out, byte[] apiVersion , byte[] correlation_id , byte[] key ){
        this.in = in ; 
        this.out = out ;
        this.apiVersion = apiVersion ;
        this.correlation_id = correlation_id;
        this.apiKey = key ;
    }

    @Override
    public void run() {
        System.out.println("============== NEW REQUEST!! ============");
        try{
            process();
        }
        catch(Exception e){
            System.out.println("Handler, IOException :" + e.getMessage());
        }
    }

    private synchronized void process(OutputStream out, ByteBuffer request) throws IOException {
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
        byte[] res = new byte[buffer.remaining()] ;
        buffer.get(res) ;

        System.out.println("Sending out the response.");
        int size = res.length ;
        byte[] respSize = ByteBuffer.allocate(4).putInt(size).array();
        out.write(respSize);
        out.write(res);
        System.out.println("reponse was sent.");
    }

    private void process()  throws IOException{
        var arr = new ByteArrayOutputStream();
        arr.write(this.correlation_id);

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

        this.out.write(respSize);
        this.out.write(res);
        System.out.println("end of processing");
    }

}
