package LoggerCore.Zefiro.ITCharacteristic;

import LoggerCore.Sweep;
import LoggerCore.themal.IFeedbackController;

public class TemperatureSweep extends Sweep {

    protected IFeedbackController _feedbackController;

    public TemperatureSweep(IFeedbackController feedbackController) {
        super();
        _feedbackController = feedbackController;
    }

    @Override
    public void sweepAction(double value) {
        _feedbackController.set_target_value(value);
    }
}
