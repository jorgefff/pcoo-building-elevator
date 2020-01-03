import static java.lang.System.*;
import pt.ua.concurrent.Mutex;
import pt.ua.gboard.*;
import pt.ua.gboard.basic.ImageGelem;
import pt.ua.gboard.basic.MutableStringGelem;

import java.awt.*;

public class Graphical {

    private static Graphical instance = null;
    private static final Mutex instanceMtx = new Mutex(true);

    protected GBoard gboard;
    protected Building b;
    protected MutableStringGelem[] floors;
    protected MutableStringGelem elevTxt;
    protected ImageGelem elevImg;

    protected static final int NUM_LAYERS = 3;
    protected static final int IMG_LAYER = 1;
    protected static final int TXT_LAYER = 2;
    protected static final int ELEV_X = 2;
    protected static final int FL_X = 1;
    protected static int CELL_HEIGHT = 10;
    protected static final int CELL_WIDTH = 1;
    protected static int floorDigits;
    protected static int elevDigits;

    private Graphical () {
        b = null;
        floors = null;
        elevTxt = null;
        elevImg = null;
        gboard = null;
    }

    public static Graphical getInstance() {
        instanceMtx.lock();
        if (instance == null) {
            instance = new Graphical();
        }
        instanceMtx.unlock();
        return instance;
    }


    public void setBuilding (Building building, int totalPeople, Color floorColor, Color elevColor) {
        assert building != null;
        assert  ImageGelem.isImage("elevator.png");

        instanceMtx.lock();

        b = building;

        // Digit amount format
        elevDigits = (""+b.getElevator().getCapacity()).length();
        floorDigits = (""+totalPeople).length();

        // Building dimensions
        CELL_HEIGHT = b.getElevator().getMovementUnit();
        int bHeight = b.getNumFloors() * CELL_HEIGHT;
        int bWidth = 4;
        gboard = new GBoard("Building elevator manager", bHeight, bWidth, NUM_LAYERS);

        // Set up floors occupancy text
        floors = new MutableStringGelem[b.getNumFloors()];
        String flDefaultTxt = String.format("%0"+floorDigits+"d", 0);
        for (int i = 0; i < b.getNumFloors(); i++) {
            floors[i] = new MutableStringGelem(flDefaultTxt, floorColor, CELL_HEIGHT, 1);
            int floorY = (b.getNumFloors()-1-i) * b.getElevator().getMovementUnit();
            gboard.draw(floors[i], floorY, FL_X, TXT_LAYER);
        }

        // Set up elevator sprite and occupancy text
        int elevY = (b.getNumFloors()-1) * CELL_HEIGHT;
        elevImg = new ImageGelem("elevator.png", gboard, 90, CELL_HEIGHT, CELL_WIDTH);
        gboard.draw(elevImg, elevY, ELEV_X, IMG_LAYER);
        String elevDefaultTxt = String.format("%0"+elevDigits+"d", 0);
        elevTxt = new MutableStringGelem(elevDefaultTxt, elevColor, CELL_HEIGHT, CELL_WIDTH);
        gboard.draw(elevTxt, elevY, ELEV_X, TXT_LAYER);

        instanceMtx.unlock();
    }

    public void updateFloor(Floor f) {
        assert floors != null;

        int n = f.getFloorNum();
        String text = String.format("%0"+floorDigits+"d", f.getOccupancy());
        floors[n].setText(text);
    }

    public void updateElevatorPpl(Elevator e) {
        assert elevTxt != null;
        assert elevImg != null;

        String text = String.format("%0"+elevDigits+"d", e.getOccupancy());
        elevTxt.setText(text);
    }

    public void updateElevatorPos(int oldPos, int newPos) {
        assert elevTxt != null;
        assert elevImg != null;

        // Converts real position to gboard position
        int gbOldPos = (b.getNumFloors()-1) * CELL_HEIGHT - oldPos;
        int gbNewPos = (b.getNumFloors()-1) * CELL_HEIGHT - newPos;

        // Performs the movement
        gboard.move(elevTxt, gbOldPos, ELEV_X, gbNewPos, ELEV_X);
        gboard.move(elevImg, gbOldPos, ELEV_X, gbNewPos, ELEV_X);
    }
}
