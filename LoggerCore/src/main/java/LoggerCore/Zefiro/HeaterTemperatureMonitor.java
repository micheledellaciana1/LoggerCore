package LoggerCore.Zefiro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.PointsStream;
import LoggerCore.themal.IFeedbackController;

public class HeaterTemperatureMonitor extends PointsStream {

    protected Zefiro _zef;
    protected IFeedbackController _feedBackController;
    protected LinkedPointAnalysis _currentHeater;
    protected LinkedPointAnalysis _targetTemperature;

    public HeaterTemperatureMonitor(Zefiro zef, IFeedbackController feedBackController, Comparable<?> KeyTemperature,
            Comparable<?> KeyCurrentHeater, Comparable<?> KeyTargetTemperature) {
        super(KeyTemperature);
        _zef = zef;
        _feedBackController = feedBackController;
        _feedBackController.setFeedbackON(true);

        _currentHeater = new LinkedPointAnalysis(KeyCurrentHeater) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                double responce = _feedBackController.responce(temperature.getY(), _zef.getCurrentHeater());
                _zef.executeCommand("Set current heater", Double.toString(responce));

                return new Point2D.Double(temperature.getX(), _zef.getCurrentHeater());
            }
        };

        _targetTemperature = new LinkedPointAnalysis(KeyTargetTemperature) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                return new Point2D.Double(temperature.getX(), _feedBackController.getTarget());
            }
        };

        getLinkedAnalysisCollection().add(_currentHeater);
        getLinkedAnalysisCollection().add(_targetTemperature);
    }

    @Override
    public Point2D acquirePoint() {
        double temperature = _zef.getTemperatureHeater();
        if (Double.isFinite(temperature)) {
            return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3,
                    _zef.getTemperatureHeater());
        } else {
            return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, -1);
        }
    }

    public XYSeries getCurrentHeaterSeries() {
        return _currentHeater.getSeries();
    }

    public XYSeries getTemperatureSeries() {
        return getSeries();
    }

    public XYSeries getTargetTemperatureSeries() {
        return _targetTemperature.getSeries();
    }
}
