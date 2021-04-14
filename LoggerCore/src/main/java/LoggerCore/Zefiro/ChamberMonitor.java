package LoggerCore.Zefiro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.ObjectStream;

public class ChamberMonitor extends ObjectStream {

    protected Zefiro _zef;
    protected LinkedPointAnalysis _TempMonitor;
    protected LinkedPointAnalysis _RHMonitor;
    protected LinkedPointAnalysis _OxygenMonitor;

    public ChamberMonitor(Zefiro zef, Comparable<?> KeyTempChamber, Comparable<?> KeyRHChamber,
            Comparable<?> KeyOxygenChamber) {
        super();

        _zef = zef;

        _TempMonitor = new LinkedPointAnalysis(KeyTempChamber) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                double temp = Double.class.cast(_zef.executeCommand("ReadTempSensor", null));
                return new Point2D.Double(Double.class.cast(newData), temp);
            }
        };

        _RHMonitor = new LinkedPointAnalysis(KeyRHChamber) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                double RH = Double.class.cast(_zef.executeCommand("ReadRHSensor", null));
                return new Point2D.Double(Double.class.cast(newData), RH);
            }
        };

        _OxygenMonitor = new LinkedPointAnalysis(KeyOxygenChamber) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                double Oxygen = Double.class.cast(_zef.executeCommand("ReadOxygenSensor", null));
                return new Point2D.Double(Double.class.cast(newData), Oxygen);
            }
        };

        getLinkedAnalysisCollection().add(_TempMonitor);
        getLinkedAnalysisCollection().add(_RHMonitor);
        getLinkedAnalysisCollection().add(_OxygenMonitor);
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

    public XYSeries getOxygenSeries() {
        return _OxygenMonitor.getSeries();
    }

    public LinkedPointAnalysis get_TempMonitor() {
        return _TempMonitor;
    }

    public LinkedPointAnalysis get_RHMonitor() {
        return _RHMonitor;
    }

    public LinkedPointAnalysis get_OxygenMonitor() {
        return _OxygenMonitor;
    }
}
