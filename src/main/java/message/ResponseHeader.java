package message;

import java.nio.ByteBuffer;

public class ResponseHeader {
    int correlationId ;
    public ResponseHeader() {
    }
    public ResponseHeader(int correlationId){
        this.correlationId = correlationId ;
    }

    public byte[] encodeCorrelationId() {
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(correlationId) ;
        byte[] id = new byte[4] ;
        buffer.get(id) ;
        return id ;
    }

    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }


}
