
public class Request implements Comparable<Request> {

    public final int floor;
    public final long timestamp;

    public Request(int floor) {
        this.floor = floor;
        this.timestamp = System.currentTimeMillis();;
    }

    @Override
    public int compareTo(Request request) {
        return Long.compare(this.timestamp, request.timestamp);
    }
}
