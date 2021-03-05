package LoggerCore.Moira.SoftwareFollower;

import java.util.Arrays;

import LoggerCore.StoppableRunnable;
import LoggerCore.Moira.Moira;
import LoggerCore.themal.FeedBackController_type1;

public class VoltageFollower extends StoppableRunnable {

    private Moira _moira;
    private FeedBackController_type1 _feedbackController;

    public VoltageFollower(Moira Moira, FeedBackController_type1 feedbackController, int executionDelay) {
        _name = "Voltage Follower";

        _feedbackController = feedbackController;
        _moira = Moira;

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

        _feedbackController.reset();
        return super.setup(arg);
    }

    @Override
    public void MiddleLoop() {
        while (true)
            try {
                double voltageCh0[] = _moira.ReadOscilloscopeSingleChannel(0);
                double AvgVoltageCh0 = Arrays.stream(voltageCh0).average().getAsDouble();

                if (_moira.getVoltageRange(0) <= 2.5)
                    _feedbackController.TOLLERATED_ERROR = 0.00085;
                else
                    _feedbackController.TOLLERATED_ERROR = 0.0085;

                _moira.setDCVoltage(0, _feedbackController.responce(AvgVoltageCh0, _moira.getOffsetVoltage(0)));
                break;
            } catch (Exception e) {
                _feedbackController.reset();
            }
    }
}
