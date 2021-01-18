package LoggerCore.Moira;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import LoggerCore.LinkedPointAnalysis;

public class LifeTimeAnalysis extends LinkedPointAnalysis {

    // todelete
    public long StartingTime = System.currentTimeMillis();

    public LifeTimeAnalysis(String Name) {
        super(Name);
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {

        ArrayList<double[]> values = (ArrayList<double[]>) newData;

        Double[] currentValues = new Double[values.get(1).length];
        for (int i = 0; i < currentValues.length; i++)
            currentValues[i] = values.get(1)[i];

        double sum = 0.;
        for (double d : currentValues)
            sum += d;

        return new Point2D.Double((System.currentTimeMillis() - StartingTime), sum / currentValues.length);
    }
}
