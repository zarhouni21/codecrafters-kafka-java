package message;

public class RequestHeader {
    private short apikey ;
    private short apiVersion ;

    private int correlationId ;

    public short getApikey() {
        return apikey;
    }

    public void setApikey(short apikey) {
        this.apikey = apikey;
    }

    public short getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(short apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }
//    private String client_id ; // is still not implemented ;
    //private byte _tagged_fields ; // additional metadata to requests or responses without changing the core structure of the protocol message



}
