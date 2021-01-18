package LoggerCore.Zefiro.SoftwareFollower;

import LoggerCore.StoppableRunnable;
import LoggerCore.Zefiro.Zefiro;
import LoggerCore.themal.IFeedbackController;

public class VoltageFollower extends StoppableRunnable {

    private Zefiro _zef;
    private IFeedbackController _feedbackController;

    public VoltageFollower(Zefiro zef, IFeedbackController feedbackController, int executionDelay) {
        _feedbackController = feedbackController;
        _zef = zef;

        _ExecutionDelay = executionDelay;
    }

    @Override
    public boolean setup(Object arg) {
        try {
            double targetVoltage = Double.valueOf(String.class.cast(arg));
            _feedbackController.set_target_value(targetVoltage);
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();

            return false;
        }

        _feedbackController.setTolleratedError(0.004);
        _feedbackController.reset();
        return super.setup(arg);
    }

    @Override
    public void MiddleLoop() {
        _zef.setVoltageFallSensor(
                _feedbackController.responce(_zef.readVoltageFallSensor(), _zef.getVoltageSensorDAC()));
    }
}
