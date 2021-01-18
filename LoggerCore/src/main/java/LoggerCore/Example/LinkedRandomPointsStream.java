package LoggerCore.Example;

import java.awt.geom.Point2D;

import LoggerCore.LinkedPointAnalysis;

public class LinkedRandomPointsStream extends LinkedPointAnalysis {

    public LinkedRandomPointsStream() {
        super("Linked Random Points Stream");
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        Point2D newPoint = (Point2D) newData;
        return new Point2D.Double(newPoint.getX(), newPoint.getY() * 2 - 3);
    }
}
