
public class Request {

    public final int floor;
    public final long timestamp;
    public final String source;

    public Request(int floor) {
        this.floor = floor;
        this.timestamp = System.currentTimeMillis();
        this.source = "NULL";
    }

    public Request(int floor, String source) {
        this.floor = floor;
        this.timestamp = System.currentTimeMillis();
        this.source = source;
    }


    @Override
    public String toString() {
        return "REQ:"+floor+"-FROM:"+source;
    }
}
