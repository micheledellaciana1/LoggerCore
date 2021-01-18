package LoggerCore.Example;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.PointsStream;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LinkedRandomPointsArrayStream extends LinkedPointAnalysis {
    public LinkedRandomPointsArrayStream() {
        super("Linked Random Points Array Stream");
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        ArrayList<Point2D> newPoints = (ArrayList<Point2D>) newData;
        double sum = 0.;
        for (int i = 0; i < newPoints.size(); i++)
            sum += newPoints.get(i).getY();

        return new Point2D.Double(System.currentTimeMillis() - GlobalVar.start, sum / newPoints.size());
    }
}
