package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.Moira.Moira;

public class LifeTimeAnalysis extends LinkedPointAnalysis {

    protected Moira _moira;
    protected double _TimeStart = 0;
    protected double _TimeFinish = 0;
    protected double _factorToLifeTime;

    public LifeTimeAnalysis(String Name, Moira moira) {
        super(Name);
        _moira = moira;

        double q = 1.602e-19;
        double Kb = 1.380649e-23;
        double idelityFactor = 1;

        _factorToLifeTime = (-idelityFactor * Kb) / q;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        double dt = _moira.getTimeBin();

        ArrayList<double[]> values = (ArrayList<double[]>) newData;

        SimpleRegression sr = new SimpleRegression(true);

        for (int i = 0; i < values.get(0).length; i++) {
            double time = i * dt;
            if (time < _TimeFinish && time > _TimeStart)
                sr.addData(i * dt, values.get(0)[i]);
        }

        double slope = sr.getSlope();
        double lifetime = _factorToLifeTime / slope;
        lifetime *= 1e6;

        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, lifetime);
    }

    public void set_TimeFinish(double _TimeFinish) {
        this._TimeFinish = _TimeFinish;
    }

    public void set_TimeStart(double _TimeStart) {
        this._TimeStart = _TimeStart;
    }

    public double get_TimeFinish() {
        return _TimeFinish;
    }

    public double get_TimeStart() {
        return _TimeStart;
    }
}
