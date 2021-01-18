package LoggerCore.Example;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import LoggerCore.PointsArrayStream;
import LoggerCore.Communication.Command;
import LoggerCore.Communication.Device;
import LoggerCore.Communication.SerialDevice;

public class RandomPointsArrayStream extends PointsArrayStream {

    Device _dev;

    public RandomPointsArrayStream() {
        super("Random Points Array Stream");
        _dev = new Device("Virtual Device");
        _dev.addCommand(new Command("Com1") {
            @Override
            public Object execute(Object arg) {
                return arg;
            }
        });
    }

    @Override
    public ArrayList<Point2D> acquirePoints() {
        ArrayList<Point2D> R = new ArrayList<Point2D>();

        for (int i = 0; i < 1000; i++)
            R.add(new Point2D.Double(i, (double) _dev.executeCommand("Com1", Math.random())));
        return R;
    }
}
