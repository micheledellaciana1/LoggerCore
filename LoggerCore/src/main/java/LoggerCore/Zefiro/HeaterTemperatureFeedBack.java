package LoggerCore.Zefiro;

import LoggerCore.StoppableRunnable;
import LoggerCore.themal.IFeedbackController;

public class HeaterTemperatureFeedBack extends StoppableRunnable {

    protected Zefiro _zef;
    protected IFeedbackController _feedBackController;

    public HeaterTemperatureFeedBack(Zefiro zef, IFeedbackController feedBackController) {
        super();
        _name = "Heater temperature Feedback";
        _zef = zef;
        _feedBackController = feedBackController;
        _feedBackController.setFeedbackON(true);
    }

    @Override
    public void MiddleLoop() {
        double temperature = _zef.getTemperatureHeater();
        if (!Double.isFinite(temperature)) {
            return;
        } else {
            double responce = _feedBackController.responce(temperature, _zef.getCurrentHeater());
            _zef.executeCommand("Set current heater", Double.toString(responce));
        }
    }
}
