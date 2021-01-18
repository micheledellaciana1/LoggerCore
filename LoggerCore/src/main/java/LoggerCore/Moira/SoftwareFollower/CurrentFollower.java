package LoggerCore.Moira.SoftwareFollower;

import java.util.Arrays;

import LoggerCore.StoppableRunnable;
import LoggerCore.Moira.Moira;
import LoggerCore.themal.FeedBackController_type1;

public class CurrentFollower extends StoppableRunnable {

    private Moira _moira;
    private FeedBackController_type1 _feedbackController;

    public CurrentFollower(Moira Moira, FeedBackController_type1 feedbackController, int executionDelay) {
        _moira = Moira;

        _feedbackController = feedbackController;
        feedbackController.TOLLERATED_ERROR = _moira.convertToCurrent(0.00085);

        _ExecutionDelay = executionDelay;
    }

    @Override
    public boolean setup(Object arg) {
        try {
            double targetCurrent = Double.valueOf(String.class.cast(arg));
            _feedbackController.set_target_value(targetCurrent);
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
                double voltageCh1[] = _moira.ReadOscilloscopeSingleChannel(1);
                double AvgVoltageCh1 = Arrays.stream(voltageCh1).average().getAsDouble();

                double AvgCurrentDUT = _moira.convertToCurrent(AvgVoltageCh1);
                _moira.setDCVoltage(0, _feedbackController.responce(AvgCurrentDUT, _moira.getOffsetVoltage(0)));
                break;
            } catch (Exception e) {
                _feedbackController.reset();
            }
    }
}
