package LoggerCore.Icaro;

import java.util.ArrayList;

import LoggerCore.StoppableRunnable;
import LoggerCore.themal.IFeedbackController;

public class TemperatureRamp extends StoppableRunnable {

    protected IFeedbackController _feedbackController;

    protected int _Ncycle;
    protected int _cycleCounter;
    protected ArrayList<Double> _Path;
    protected int _indexPath;

    public TemperatureRamp(IFeedbackController feedbackController) {
        super();
        setName("Temperature Ramp");
        _feedbackController = feedbackController;
    }

    @Override
    public boolean setup(Object arg) {
        try {
            String[] par = String.class.cast(arg).split(" ");
            calculate_PATH(Double.valueOf(par[0]), Double.valueOf(par[1]), Double.valueOf(par[2]),
                    Boolean.valueOf(par[4]));
            _Ncycle = Integer.valueOf(par[3]);
            _ExecutionDelay = Integer.valueOf(par[5]);
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
            return false;
        }

        if (_Ncycle < 0)
            _Ncycle = Integer.MAX_VALUE;

        _cycleCounter = 0;
        _indexPath = 0;

        return true;
    }

    @Override
    public void MiddleLoop() {
        _feedbackController.set_target_value(_Path.get(_indexPath++));
    }

    @Override
    public void EndLoop() {
        if (_indexPath == _Path.size()) {
            _indexPath = 0;
            _cycleCounter++;
            if (_cycleCounter >= _Ncycle)
                kill();
        }
    }

    @Override
    public void OnClosing() {
        super.OnClosing();
    }

    public void calculate_PATH(double Start, double Finish, double step, boolean UpDown) {
        if ((Finish - Start) * step > 0) {
            _Path = new ArrayList<Double>();

            if (step > 0) {
                for (double i = Start; i < Finish; i += step)
                    _Path.add(i);

                for (double i = Finish; i > Start && UpDown; i -= step)
                    _Path.add(i);
            }

            if (step < 0) {
                for (double i = Start; i > Finish; i += step)
                    _Path.add(i);

                for (double i = Finish; i < Start && UpDown; i -= step)
                    _Path.add(i);
            }
        }
    }
}
