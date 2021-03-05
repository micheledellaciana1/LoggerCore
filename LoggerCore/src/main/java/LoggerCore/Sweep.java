package LoggerCore;

import java.util.ArrayList;

public abstract class Sweep extends StoppableRunnable {

    protected int _Ncycle;
    protected int _cycleCounter;
    protected ArrayList<Double> _Path;
    protected int _indexPath;

    public Sweep() {
        super();

        _firstDelay = true;
        _secondDelay = false;
    }

    @Override
    public boolean setup(Object arg) {
        try {
            String[] pars = String.class.cast(arg).split(" ");
            InitSweep(Double.valueOf(pars[0]), Double.valueOf(pars[1]), Double.valueOf(pars[2]),
                    Integer.valueOf(pars[3]), Boolean.valueOf(pars[4]), Integer.valueOf(pars[5]));
            return true;
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
            return false;
        }
    }

    protected boolean _useLogPath = false;

    public void InitSweep(double Start, double Finish, double StepOrFactor, int times, boolean UpDown,
            int millisDelay) {
        if (_useLogPath)
            calculate_LogPATH(Start, Finish, StepOrFactor, UpDown);
        else
            calculate_PATH(Start, Finish, StepOrFactor, UpDown);

        _Ncycle = times;
        _ExecutionDelay = millisDelay;

        if (_Ncycle < 0)
            _Ncycle = Integer.MAX_VALUE;

        _cycleCounter = 0;
        _indexPath = 0;
    }

    public abstract void sweepAction(double value);

    @Override
    public void StartLoop() {
        sweepAction(_Path.get(_indexPath++));
    }

    @Override
    public void MiddleLoop() {
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

    protected void calculate_PATH(double Start, double Finish, double step, boolean UpDown) {
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

    protected void calculate_LogPATH(double Start, double Finish, double factor, boolean UpDown) {
        if ((Finish - Start) > 0 && factor != 1) {
            _Path = new ArrayList<Double>();

            if (factor > 1) {
                for (double i = Start; i < Finish; i *= factor)
                    _Path.add(i);

                for (double i = Finish; i > Start && UpDown; i /= factor)
                    _Path.add(i);
            }
        }
    }
}
