package message;

import java.nio.ByteBuffer;

public class Response {
    private ResponseHeader header ;
    private ResponseBody body ;

    public Response() {
        this.header = new ResponseHeader();
        this.body = new ResponseBody();
    }

    public static Response fromRequest(Request req){
        Response output = new Response() ;
        output.getHeader().setCorrelationId(req.getHeader().getCorrelationId());
        if(req.getHeader().getApiVersion()<=-1 ||req.getHeader().getApiVersion()>=5) {
            output.getBody().setError_code( (short) 35 );
        }
        else{
            output.getBody().setError_code((short)0);
        }
        output.getBody().setArrayLength(2); // TODO: logic of all what follows this, except the outer_tagged_fields, should be changed to handle an array of apiKeys instead just one *low priority for now.
        output.getBody().setApiKey((short) 18);
        output.getBody().setMinVersion((short)3);
        output.getBody().setMaxVersion((short)4);
        output.getBody().setArray_tagged_fields((byte)0);
        output.getBody().setThrottle_time(0);
        output.getBody().setOuter_tagged_fields((byte)0);

        return output ;
    }

    public byte[] encodeResponse(){
        ByteBuffer buffer = ByteBuffer.allocate(1024)
                .putInt(header.getCorrelationId())
                .putShort(body.getError_code())
                .put((byte) body.getArrayLength())
                .putShort(body.getApiKey())
                .putShort(body.getMinVersion())
                .putShort(body.getMaxVersion())
                .put(body.getArray_tagged_fields())
                .putInt(body.getThrottle_time())
                .put(body.getOuter_tagged_fields())
                .flip() ;

        byte[] resp = new byte[buffer.remaining()] ;
        buffer.get(resp) ;

        return resp ;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }
}
