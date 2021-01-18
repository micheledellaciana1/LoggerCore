package LoggerCore.Zefiro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.ObjectStream;

public class VoltageCurrentSensorMonitor extends ObjectStream {

    protected Zefiro _zef;

    protected double _voltage;
    protected double _current;
    protected double _resistance;

    protected LinkedPointAnalysis _voltageMonitor;
    protected LinkedPointAnalysis _currentMonitor;
    protected LinkedPointAnalysis _resistanceMonitor;

    public VoltageCurrentSensorMonitor(Zefiro zef, Comparable<?> keyVoltage, Comparable<?> keyCurrent,
            Comparable<?> keyResistance) {
        super();
        _zef = zef;

        _voltageMonitor = new LinkedPointAnalysis(keyVoltage) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(Double.class.cast(newData), _voltage);
            }
        };

        _currentMonitor = new LinkedPointAnalysis(keyCurrent) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(Double.class.cast(newData), _current);
            }
        };

        _resistanceMonitor = new LinkedPointAnalysis(keyResistance) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(Double.class.cast(newData), _resistance);
            }
        };

        getLinkedAnalysisCollection().add(_voltageMonitor);
        getLinkedAnalysisCollection().add(_currentMonitor);
        getLinkedAnalysisCollection().add(_resistanceMonitor);
    }

    @Override
    public Object acquireObject() {
        _voltage = _zef.readVoltageFallSensor();
        _current = _zef.readCurrentSensor();

        try {
            _resistance = _voltage / _current;
            if (!Double.isFinite(_resistance))
                _resistance = -1;
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
            _resistance = -1.;
        }

        return (System.currentTimeMillis() - GlobalVar.start) * 1e-3;
    }

    public XYSeries getVoltageSeries() {
        return _voltageMonitor.getSeries();
    }

    public XYSeries getCurrentSeries() {
        return _currentMonitor.getSeries();
    }

    public XYSeries getResistanceSeries() {
        return _resistanceMonitor.getSeries();
    }

    public LinkedPointAnalysis get_voltageMonitor() {
        return _voltageMonitor;
    }

    public LinkedPointAnalysis get_currentMonitor() {
        return _currentMonitor;
    }

    public LinkedPointAnalysis get_resistanceMonitor() {
        return _resistanceMonitor;
    }
}
