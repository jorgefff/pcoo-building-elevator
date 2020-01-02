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

    private Graphical () {
        b = null;
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

    public void setBuilding (Building b) {
        instanceMtx.lock();
        this.b = b;
        instanceMtx.unlock();
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
