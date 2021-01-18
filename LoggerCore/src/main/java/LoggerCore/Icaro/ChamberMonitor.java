package LoggerCore.Icaro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.ObjectStream;

public class ChamberMonitor extends ObjectStream {

    protected Icaro _ica;
    protected LinkedPointAnalysis _TempMonitor;
    protected LinkedPointAnalysis _RHMonitor;

    public ChamberMonitor(Icaro ica, Comparable<?> KeyTempChamber, Comparable<?> KeyRHChamber) {
        super();

        _ica = ica;

        _TempMonitor = new LinkedPointAnalysis(KeyTempChamber) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                double temp = Double.class.cast(_ica.executeCommand("ReadTempSensor", null));
                return new Point2D.Double(Double.class.cast(newData), temp);
            }
        };

        _RHMonitor = new LinkedPointAnalysis(KeyRHChamber) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                double RH = Double.class.cast(_ica.executeCommand("ReadRHSensor", null));
                return new Point2D.Double(Double.class.cast(newData), RH);
            }
        };

        getLinkedAnalysisCollection().add(_TempMonitor);
        getLinkedAnalysisCollection().add(_RHMonitor);
    }

    @Override
    public Object acquireObject() {
        return (System.currentTimeMillis() - GlobalVar.start) * 1e-3;
    }

    public XYSeries getTempSeries() {
        return _TempMonitor.getSeries();
    }

    public XYSeries getRHSeries() {
        return _RHMonitor.getSeries();
    }
}
