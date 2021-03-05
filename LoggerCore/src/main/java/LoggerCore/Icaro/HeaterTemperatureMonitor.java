package LoggerCore.Icaro;

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.PointsStream;
import LoggerCore.themal.IFeedbackController;

public class HeaterTemperatureMonitor extends PointsStream {

    protected Icaro _ica;
    protected IFeedbackController _feedBackController;
    protected LinkedPointAnalysis _VoltageHeater;
    protected LinkedPointAnalysis _targetTemperature;

    public HeaterTemperatureMonitor(Icaro ica, IFeedbackController feedBackController, Comparable<?> KeyTemperature,
            Comparable<?> KeyVoltageHeater, Comparable<?> KeyTargetTemperature) {
        super(KeyTemperature);
        _ica = ica;
        _feedBackController = feedBackController;
        _feedBackController.setFeedbackON(true);

        _VoltageHeater = new LinkedPointAnalysis(KeyVoltageHeater) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                double responce = _feedBackController.responce(temperature.getY(), _ica.getVoltageHeater());
                _ica.executeCommand("Set voltage heater", Double.toString(responce));

                return new Point2D.Double(temperature.getX(), _ica.getVoltageHeater());
            }
        };

        _targetTemperature = new LinkedPointAnalysis(KeyTargetTemperature) {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                Point2D temperature = Point2D.Double.class.cast(newData);
                return new Point2D.Double(temperature.getX(), _feedBackController.getTarget());
            }
        };

        getLinkedAnalysisCollection().add(_VoltageHeater);
        getLinkedAnalysisCollection().add(_targetTemperature);
    }

    @Override
    public Point2D acquirePoint() {
        return new Point2D.Double((System.currentTimeMillis() - GlobalVar.start) * 1e-3, _ica.getTemperatureHeater());
    }

    public XYSeries getVoltagetHeaterSeries() {
        return _VoltageHeater.getSeries();
    }

    public XYSeries getTemperatureSeries() {
        return getSeries();
    }

    public XYSeries getTargetTemperatureSeries() {
        return _targetTemperature.getSeries();
    }
}
