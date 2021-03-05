package LoggerCore;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import java.awt.geom.*;
import java.awt.*;

public abstract class LinkedArrayAnalysis extends LinkedAnalysis {

    protected XYSeries _points;
    private volatile boolean _flagIsRendering = false;

    public LinkedArrayAnalysis(Comparable<?> key) {
        super(key.toString());
        _points = new XYSeries(key, false);
    }

    private synchronized void setflagIsRendering(boolean newValue) {
        _flagIsRendering = newValue;
    }

    private synchronized boolean isRendering() {
        return _flagIsRendering;
    }

    @Override
    public Object ExecuteAnalysis(Object newData) {
        final ArrayList<Point2D> _newPoints = ExecuteArrayAnalysis(newData);

        if (_newPoints == null)
            return null;

        if (isRendering())
            return _newPoints;
        else {
            setflagIsRendering(true);

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _points.setNotify(false);
                    _points.clear();
                    for (int i = 0; i < _newPoints.size(); i++)
                        _points.add(_newPoints.get(i).getX(), _newPoints.get(i).getY());
                    _points.setNotify(true);
                    setflagIsRendering(false);
                }
            });
        }

        return _newPoints;
    }

    public abstract ArrayList<Point2D> ExecuteArrayAnalysis(Object newData);

    public XYSeries getSeries() {
        return _points;
    }
}
