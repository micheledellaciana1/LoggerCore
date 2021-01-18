package LoggerCore.Moira;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;

public class AvgCurrentDUTAnalysis extends LinkedPointAnalysis {

    protected Moira _moira;

    public AvgCurrentDUTAnalysis(String Name, Moira moira) {
        super(Name);
        _moira = moira;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);
        double avgCurrent = Arrays.stream(values.get(1)).average().getAsDouble();
        avgCurrent = _moira.convertToCurrent(avgCurrent);
        return new Point2D.Double(System.currentTimeMillis() - GlobalVar.start, avgCurrent);
    }
}
