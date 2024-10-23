import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class HandleClient extends Thread{
    InputStream in ; 
    OutputStream out ;

    List<ByteBuffer> waitingList ;

    public HandleClient(InputStream in , OutputStream out , List<ByteBuffer> list){
        this.in = in ; 
        this.out = out ;
        waitingList = list ;
    }

    public List<ByteBuffer> getWaitingList() {
        return waitingList;
    }

    @Override
    public void run() {
        System.out.println("============== NEW REQUEST!! ============");

        try{
            while(waitingList.isEmpty()) wait(1000);
            for (ByteBuffer req : waitingList){
                process(out,req);
                System.out.println("reponse was sent.");
            }
//            ByteBuffer req = ByteBuffer.allocate(regLength).put(dataIn.readNBytes(regLength)).rewind() ;
//            short apiKey = req.getShort() ;
//            short apiVersion = req.getShort() ;
//            int correlationId = req.getInt() ;
//            System.out.println("request data : \n"+"\t api key: "+apiKey+"\n\t api version: "+apiVersion+"\n\t correlation Identifier: "+correlationId+"\nend of request.");
//            short error = 0  ;
//
//            if(apiVersion<0 ||apiVersion>4){
//                error = 35 ;
//            }
//
//            short API_KEY = 18 ;
//            short MIN_VERSION = 3 ;
//            short MAX_VERSION = 4 ;
//            System.out.println("filling out the response. ");
//            ByteBuffer buffer = ByteBuffer.allocate(1024).putInt(correlationId)
//                    .putShort(error)
//                    .put((byte) 2)
//                    .putShort(API_KEY)
//                    .putShort(MIN_VERSION)
//                    .putShort(MAX_VERSION)
//                    .put((byte) 0)
//                    .putInt(0)
//                    .put((byte) 0)
//                    .flip() ;
//
//            byte[] res = new byte[buffer.remaining()] ;
//            buffer.get(res) ;
//
//            System.out.println("Sending out the response.");
//            System.out.println("response's size : " + res.length);
//            out.write(res.length);
//            out.write(res);
//            System.out.println("reponse was sent.");
        }
        catch(Exception e){
            System.out.println("Handler, IOException :" + e.getMessage());
        }
    }

    private static void process(OutputStream out, ByteBuffer request) throws IOException {
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
        System.out.println("response's size : " + res.length);
        out.write(res.length);
        out.write(res);
        System.out.println("reponse was sent.");
    }

}
