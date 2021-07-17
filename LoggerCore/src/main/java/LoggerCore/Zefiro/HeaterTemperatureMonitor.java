package LoggerCore.Zefiro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.PointsStream;
import LoggerCore.themal.IFeedbackController;

public class HeaterTemperatureMonitor extends PointsStream {

    protected Zefiro _zef;
    protected HeaterTemperatureFeedBack _feedbackRunnable;
    protected LinkedPointAnalysis _currentHeater;
    protected LinkedPointAnalysis _targetTemperature;

    public HeaterTemperatureMonitor(Zefiro zef, IFeedbackController feedBackController, Comparable<?> KeyTemperature,
            Comparable<?> KeyCurrentHeater, Comparable<?> KeyTargetTemperature) {
        super(KeyTemperature);
        _zef = zef;

        _feedbackRunnable = new HeaterTemperatureFeedBack(zef, feedBackController);
        _feedbackRunnable.setExecutionDelay(100);
        _currentHeater = new LinkedPointAnalysis(KeyCurrentHeater) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                return new Point2D.Double(temperature.getX(), _zef.getCurrentHeater());
            }
        };

        _targetTemperature = new LinkedPointAnalysis(KeyTargetTemperature) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                return new Point2D.Double(temperature.getX(), _feedbackRunnable._feedBackController.getTarget());
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

    public XYSeries getTargetTemperatureSeries() {
        return _targetTemperature.getSeries();
    }

    public XYSeries getTemperatureSeries() {
        return getSeries();
    }

    public HeaterTemperatureFeedBack get_feedbackRunnable() {
        return _feedbackRunnable;
    }
}
