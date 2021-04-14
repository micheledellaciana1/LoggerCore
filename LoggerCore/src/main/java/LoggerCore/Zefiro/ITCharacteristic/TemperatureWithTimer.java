package LoggerCore.Zefiro.ITCharacteristic;

import LoggerCore.StoppableRunnable;
import LoggerCore.themal.IFeedbackController;

public class TemperatureWithTimer extends StoppableRunnable {

    protected IFeedbackController _feedbackController;
    protected double _TimeToWait;

    public TemperatureWithTimer(IFeedbackController feedbackController) {
        _feedbackController = feedbackController;
        _firstDelay = true;
        _secondDelay = false;

        _ExecutionDelay = 1000;
    }

    private double _startTime;

    @Override
    public boolean setup(Object arg) {
        try {
            String[] pars = String.class.cast(arg).split(" ");
            _feedbackController.set_target_value(Double.valueOf(pars[0]));
            _TimeToWait = Double.valueOf(pars[1]) * 1000 * 60;
            _startTime = System.currentTimeMillis();
            return true;
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
            return false;
        }
    }

    @Override
    public void MiddleLoop() {
        if (System.currentTimeMillis() - _startTime > _TimeToWait) {
            kill();
        }
    }

    @Override
    public void OnClosing() {
        _feedbackController.set_target_value(0);
        super.OnClosing();
    }
}
