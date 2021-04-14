package LoggerCore;

import org.jfree.data.xy.XYSeries;

import LoggerCore.JFreeChartUtil.JFreeChartUtil;

import java.awt.EventQueue;
import java.awt.geom.*;
import java.util.ArrayList;

public abstract class PointsArrayStream extends ObjectStream {

    protected XYSeries _points;
    private volatile boolean _flagIsRendering = false;

    public PointsArrayStream(Comparable<?> KeySeries) {
        super();
        _points = new XYSeries(KeySeries, false);
    }

    private synchronized void setflagIsRendering(boolean newValue) {
        _flagIsRendering = newValue;
    }

    private synchronized boolean isRendering() {
        return _flagIsRendering;
    }

    @Override
    public ArrayList<Point2D> acquireObject() {
        final ArrayList<Point2D> _newPoints = acquirePoints();

        if (isRendering())
            return _newPoints;
        else {
            setflagIsRendering(true);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _points.setNotify(false);
                    _points.clear();
                    for (int i = 0; i < _newPoints.size(); i++) {
                        if (JFreeChartUtil.checkPointValidity(_newPoints.get(i)))
                            _points.add(_newPoints.get(i).getX(), _newPoints.get(i).getY());
                    }
                    _points.setNotify(true);
                    setflagIsRendering(false);
                }
            });
        }

        return _newPoints;
    }

    public abstract ArrayList<Point2D> acquirePoints();

    public XYSeries getSeries() {
        return _points;
    }
}
