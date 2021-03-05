package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;

import LoggerCore.LinkedArrayAnalysis;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.Moira.Moira;

public class LifeTimeAnalysis extends LinkedPointAnalysis {

    protected Moira _moira;
    protected double _xLowPercent;
    protected double _xHighPercent;

    public LifeTimeAnalysis(String Name, Moira moira) {
        super(Name);
        _moira = moira;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        double dt = _moira.getTimeBin();

        ArrayList<double[]> values = (ArrayList<double[]>) newData;

        ArrayList<WeightedObservedPoint> VoltageValues = new ArrayList<WeightedObservedPoint>();

        int idxLow = (int) (_xLowPercent * values.get(0).length);
        int idxHigh = (int) (_xHighPercent * values.get(0).length);

        for (int i = idxLow; i < idxHigh; i++)
            VoltageValues.add(new WeightedObservedPoint(1, i * dt, values.get(0)[i]));

        return new Point2D.Double();
    }
}
