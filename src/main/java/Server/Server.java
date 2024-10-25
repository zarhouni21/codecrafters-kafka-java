package Server;

import message.PrimitiveOperations;
import message.Request;
import message.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private final int port = 9092 ;
    ServerSocket server  ;
    ArrayList<Request> requests ;

    public Server() throws IOException {
        this.server = new ServerSocket(port) ;
        server.setReuseAddress(true);
        this.requests = new ArrayList<Request>() ;
    }

    @Override
    public void run() {
        handleConnection();
    }

    private void handleClient(){

    }

    private void handleConnection(){
        while(true){
            try{
                Socket client = server.accept() ;
                System.out.println("SERVER: new connection have been made : ===========================");
                while(client.getInputStream()!=null){
                    Request request = new Request() ;
                    request.readRequestFromStream(client.getInputStream());
                    push(request);
                    Response response = Response.fromRequest(request) ;
                    int responseLength = response.encodeResponse().length ;
                    client.getOutputStream().write(PrimitiveOperations.fromIntToByteArray(responseLength));
                    client.getOutputStream().write(response.encodeResponse());
                    client.getOutputStream().flush();
                }
            }catch (IOException e){
                System.out.println("SERVER, error : " + e.toString());
            }
        }
    }

    public synchronized Request pop(){
        if(!requests.isEmpty()) return requests.remove(0) ;
        return null ;
    }
    public synchronized void push(Request r){
        if(r!=null){
            requests.add(r);
            System.out.println("NEW REQUEST : request's correlation is :" + r.getHeader().getCorrelationId());
            System.out.println("NEW REQUEST : request's API KEY is :" + r.getHeader().getApiVersion());
        }
        else{
            System.out.println("apparently there is a header that is being empty that have been made??");
        }
    }
}
