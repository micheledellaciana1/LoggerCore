package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;

import LoggerCore.LinkedPointAnalysis;
import LoggerCore.Moira.Moira;

public class AvgCurrentDUTAnalysis extends LinkedPointAnalysis {

    protected Moira _moira;

    public AvgCurrentDUTAnalysis(Moira moira) {
        super("Avg Current DUT");
        _moira = moira;
    }

    @Override
    public Point2D ExecutePointAnalysis(Object newData) {
        Point2D.Double value = Point2D.Double.class.cast(newData);
        double avgCurrent = _moira.convertToCurrent(value.getY());
        return new Point2D.Double(value.getX(), avgCurrent);
    }
}
