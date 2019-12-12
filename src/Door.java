
public class Door {

    protected boolean open;     // State of doors
    protected long sensor;     // Door sensor

    public Door() {
        open = false;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }
}
