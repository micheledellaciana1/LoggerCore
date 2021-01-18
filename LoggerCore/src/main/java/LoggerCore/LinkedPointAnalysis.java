package LoggerCore;

import java.awt.geom.*;
import java.awt.EventQueue;

import org.jfree.data.xy.XYSeries;

public abstract class LinkedPointAnalysis extends LinkedAnalysis {
    XYSeries _points;
    private volatile boolean _flagIsRendering = false;

    public LinkedPointAnalysis(Comparable<?> key) {
        super(key.toString());
        _points = new XYSeries(key);
    }

    private synchronized void setflagIsRendering(boolean newValue) {
        _flagIsRendering = newValue;
    }

    private synchronized boolean isRendering() {
        return _flagIsRendering;
    }

    @Override
    public Point2D ExecuteAnalysis(final Object newData) {
        final Point2D _ExtractedPoint = ExecutePointAnalysis(newData);

        _points.setNotify(false);
        _points.add(_ExtractedPoint.getX(), _ExtractedPoint.getY());

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

        return _ExtractedPoint;
    }

    public abstract Point2D ExecutePointAnalysis(Object newData);

    public XYSeries getSeries() {
        return _points;
    }
}
