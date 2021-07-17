package LoggerCore.Icaro;

import java.awt.geom.Point2D;

import LoggerCore.GlobalVar;
import LoggerCore.PointsStream;

public class BoardTemperature extends PointsStream {

    private Icaro _ica;

    public BoardTemperature(Comparable<?> KeySeries, Icaro ica) {
        super(KeySeries);
        _ica = ica;
        setExecutionDelay(1000);
    }

    @Override
    public Point2D acquirePoint() {
        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, _ica.getTemperatureBoard());
    }
}