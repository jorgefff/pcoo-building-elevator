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


    public void setBuilding (Building building, int totalPeople, Color floorColor, Color elevColor) { //, Color floorColor, Color elevatorColor) {
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
        for (int i = 0; i < b.getNumFloors(); i++) {
            floors[i] = new MutableStringGelem("0", floorColor, CELL_HEIGHT, 1);
            int floorY = (b.getNumFloors()-1-i) * b.getElevator().getMovementUnit();
            gboard.draw(floors[i], floorY, FL_X, TXT_LAYER);
        }

        // Set up elevator sprite and occupancy text
        int elevY = (b.getNumFloors()-1) * CELL_HEIGHT;
        elevImg = new ImageGelem("elevator.png", gboard, 90, CELL_HEIGHT, CELL_WIDTH);
        gboard.draw(elevImg, elevY, ELEV_X, IMG_LAYER);
        elevTxt = new MutableStringGelem("0", elevColor, CELL_HEIGHT, CELL_WIDTH);
        gboard.draw(elevTxt, elevY, ELEV_X, TXT_LAYER);

        instanceMtx.unlock();
    }

    public void updateFloor(Floor f) {
        assert floors != null;

        int n = f.getFloorNum();
        String text = ""+f.getOccupancy();
        floors[n].setText(text);
    }

    public void updateElevatorPpl(Elevator e) {
        assert elevTxt != null;
        assert elevImg != null;

        String text = ""+e.getOccupancy();
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

    public void start() {
        int numFloors = 10;
        MutableStringGelem[] floors = new MutableStringGelem[numFloors];

        int numOfLines = numFloors * 10;
        int numOfCols = 4;

        GBoard board = new GBoard("Test Mutable", numOfLines, numOfCols, 3);

        for (int i = 0; i < numFloors; i++) {
            Integer r = i*1000;// + randInt(100);
            String text = r.toString();
            floors[i] = new MutableStringGelem(text, Color.green, 10, 1);
            int floorPos = i * 10;
            board.draw(floors[i], floorPos, 1, 1);
        }
        ImageGelem ele = null;
        MutableStringGelem eleOcc = null;
        if (ImageGelem.isImage("elevator.png")) {
            double cellOccupation = 90;
            int iNumOfLines = 10;
            int iNumOfCols = 1;
            ele = new ImageGelem("elevator.png", board, cellOccupation, iNumOfLines, iNumOfCols);
            int x = 2;
            int y = (numFloors-1) * 10;
            board.draw(ele, y, x, 1);
            eleOcc = new MutableStringGelem("0", Color.green, 10, 1);
            board.draw(eleOcc, y, x, 2);
        }
        else {
            System.out.println("Elevator image not found!");
            System.exit(1);
        }

        int x = 2;
        int pos = (numFloors-1) * 10;
        int newPos = pos;
        for(int i = 0; i < (numFloors-1)*10; i++)
        {
            newPos -= 1;
            board.sleep(200); // ms
//            floors[randInt(numFloors-1)].setText(""+8888);
            board.move(ele, pos, x, newPos, x);
            board.move(eleOcc, pos, x, newPos, x);
            pos = newPos;
        }
    }

}
