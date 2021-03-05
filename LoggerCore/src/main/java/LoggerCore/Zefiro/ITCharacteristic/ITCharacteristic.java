package LoggerCore.Zefiro.ITCharacteristic;

import java.awt.geom.Point2D;
import javax.swing.JMenu;

import LoggerCore.LoggerFrameSweep;
import LoggerCore.Zefiro.Zefiro;
import LoggerCore.themal.IFeedbackController;

public class ITCharacteristic extends LoggerFrameSweep {

    private Zefiro _zef;
    private IFeedbackController _feedbackController;
    JMenu analysisMenu;

    public ITCharacteristic(Zefiro zef, IFeedbackController feedbackController) {
        super("ITCharacteristic", "Temperature[°C]", "Current[mA]");
        _zef = zef;
        _feedbackController = feedbackController;

        _logger.addToAutosave("ITCharacteristic");
    }

    @Override
    public Point2D acquirePoint() {
        double temperature = _zef.getTemperatureHeater();
        double current = _zef.readCurrentSensor();
        return new Point2D.Double(temperature, current);
    }

    @Override
    public void sweepAction(double value) {
        _feedbackController.set_target_value(value);
    }
}