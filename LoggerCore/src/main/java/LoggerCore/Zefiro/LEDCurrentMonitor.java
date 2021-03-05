package LoggerCore.Zefiro;

import java.awt.geom.Point2D;

import LoggerCore.GlobalVar;
import LoggerCore.PointsStream;

public class LEDCurrentMonitor extends PointsStream {

    private Zefiro _zef;

    public LEDCurrentMonitor(Comparable<?> KeySeries, Zefiro zef) {
        super(KeySeries);
        _zef = zef;
    }

    @Override
    public Point2D acquirePoint() {
        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, _zef._CurrentOutpu2);
    }
}
