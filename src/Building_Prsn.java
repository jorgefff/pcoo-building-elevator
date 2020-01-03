public interface Building_Prsn {

    @Override
    public String toString();

    /**
     * Returns number of floors
     * @return
     */
    public int getNumFloors();

    /**
     * Returns a floor
     * @param n Number of floor wanted
     * @return
     */
    public Floor getFloor(int n);
    /**
     * Gets elevator instance
     * @return
     */
    public Elevator getElevator();


    /**
     * Person enters a floor
     * @param p Person must not be inside floor
     * @return returns floor entered
     */
    public Floor enterFloor (Person p);

    /**
     * Person called elevator on a floor
     * Wakes up controller
     */
    public void callElevator();
}
