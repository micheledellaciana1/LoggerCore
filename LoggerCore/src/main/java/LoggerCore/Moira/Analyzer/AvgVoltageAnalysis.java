package LoggerCore.Moira.Analyzer;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.ObjectStream;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AvgVoltageAnalysis extends LinkedPointAnalysis {

    protected int _ChIdx;

    public AvgVoltageAnalysis(int idx) {
        super("Avg Voltage Ch. " + idx);
        _ChIdx = idx;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);

        double avgVoltage = Arrays.stream(values.get(_ChIdx)).average().getAsDouble();
        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, avgVoltage);
    }
}
