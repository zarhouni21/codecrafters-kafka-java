package message;

import java.io.IOException;
import java.io.InputStream;

public class Request {
    private int length ;
    private RequestHeader header ;


    public Request(){
        length = 0 ;
        this.header = new RequestHeader() ;
    }

    public void readRequestFromStream(InputStream rawRequest){
        try{
            this.length = fromByteArrayToInt(rawRequest.readNBytes(4)) ;
            this.header.setApikey(fromByteArrayToShort(rawRequest.readNBytes(2)));
            this.header.setApiVersion(fromByteArrayToShort(rawRequest.readNBytes(2)));
            this.header.setCorrelationId(fromByteArrayToInt(rawRequest.readNBytes(4)));

        } catch (IOException e){
            System.out.println("REQUEST Service, error: a problem occurred constructing the request : "+ e.toString());
        }
    }

    // TODO: Implement them from the PrimitiveOperation class, *later.
    public Short fromByteArrayToShort(byte[] input){
        return (short) ((input[0]<<8) | (input[1]&0xFF)) ;
    }

    public int fromByteArrayToInt(byte[] input){
        int output =  0;
        for(byte b : input){ output = (output<<8)+(b&0xFF) ; }
        return  output ;
    }


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }
}
