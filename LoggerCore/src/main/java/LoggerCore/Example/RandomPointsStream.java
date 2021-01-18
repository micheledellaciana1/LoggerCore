package LoggerCore.Example;

import java.awt.geom.Point2D;
import java.awt.EventQueue;

import LoggerCore.GlobalVar;
import LoggerCore.PointsStream;

public class RandomPointsStream extends PointsStream {

    public RandomPointsStream() {
        super("Random");
    }

    @Override
    public Point2D acquirePoint() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            if (verbose)
                e.printStackTrace();
        }

        if (Math.random() < 0.001) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        _points.clear();
                    }
                });
            } catch (Exception e) {
                if (verbose)
                    e.printStackTrace();
            }
        }

        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, Math.random());
    }
}
