package LoggerCore;

import org.jfree.data.xy.XYSeries;

import LoggerCore.JFreeChartUtil.JFreeChartUtil;

import java.awt.geom.*;
import java.awt.EventQueue;

public abstract class PointsStream extends ObjectStream {

    protected XYSeries _points;
    private volatile boolean _flagIsRendering = false;

    public PointsStream(Comparable<?> KeySeries) {
        super();
        _points = new XYSeries(KeySeries, false);
        setName(KeySeries.toString());
    }

    private synchronized void setflagIsRendering(boolean newValue) {
        _flagIsRendering = newValue;
    }

    private synchronized boolean isRendering() {
        return _flagIsRendering;
    }

    @Override
    public Point2D acquireObject() {
        final Point2D _newPoint = acquirePoint();

        _points.setNotify(false);
        if (JFreeChartUtil.checkPointValidity(_newPoint))
            _points.add(_newPoint.getX(), _newPoint.getY());

        if (!isRendering()) {
            setflagIsRendering(true);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _points.setNotify(true);
                    setflagIsRendering(false);
                }
            });
        }

        return _newPoint;
    }

    public abstract Point2D acquirePoint();

    public XYSeries getSeries() {
        return _points;
    }
}
