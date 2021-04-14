package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.Moira.Moira;

public class LifeTimeAnalysis extends LinkedPointAnalysis {

    protected Moira _moira;

    public LifeTimeAnalysis(String Name, Moira moira) {
        super(Name);
        _moira = moira;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        double dt = _moira.getTimeBin();

        ArrayList<double[]> values = (ArrayList<double[]>) newData;

        double startingCurrentValue = values.get(1)[0];
        int indexOff = 0;

        for (int i = 0; i < values.get(1).length; i++)
            if (values.get(i)[i] / startingCurrentValue < 0.01) {
                indexOff = i;
                break;
            }

        SimpleRegression sr = new SimpleRegression(true);

        for (int i = indexOff; i < values.get(0).length; i++)
            sr.addData(i * dt, values.get(0)[i]);

        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, sr.getSlope());
    }
}
