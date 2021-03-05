package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import LoggerCore.LinkedArrayAnalysis;

public class LissajousAnalysis extends LinkedArrayAnalysis {

    public LissajousAnalysis() {
        super("Lissajous curve");
    }

    @Override
    public ArrayList<Point2D> ExecuteArrayAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);

        ArrayList<Point2D> result = new ArrayList<Point2D>();

        for (int i = 0; i < values.get(0).length; i++) {
            result.add(new Point2D.Double(values.get(0)[i], values.get(1)[i]));
        }

        return result;
    }
}
