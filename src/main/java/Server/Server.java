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

    public Server() throws IOException {
        this.server = new ServerSocket(port) ;
        server.setReuseAddress(true);
    }

    @Override
    public void run() {
        handleConnection();
    }

    private void handleConnection(){
        try{
            Socket client = server.accept() ;
            System.out.println("SERVER, message: new connection have been made : ===========================");
            while(client!=null){ // to handle multiple requests from the client
                Request request = new Request() ;
                request.readRequestFromStream(client.getInputStream());
                System.out.println("SERVER, new request : request's correlation Id is:" + request.getHeader().getCorrelationId());
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
