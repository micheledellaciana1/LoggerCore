package LoggerCore.Moira;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.ObjectStream;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AvgVoltageDUTAnalysis extends LinkedPointAnalysis {

    public AvgVoltageDUTAnalysis(String Name) {
        super(Name);
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);
        double avgVoltage = Arrays.stream(values.get(0)).average().getAsDouble();
        return new Point2D.Double(System.currentTimeMillis() - GlobalVar.start, avgVoltage);
    }
}
