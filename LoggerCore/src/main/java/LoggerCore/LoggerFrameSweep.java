package LoggerCore;

import java.util.ArrayList;
import java.awt.EventQueue;
import java.awt.event.*;

import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;

public abstract class LoggerFrameSweep extends PointsStream {

    protected LoggerFrame _logger;
    protected String _xUnit;
    protected String _yUnit;

    protected int _Ncycle;
    protected int _cycleCounter;
    protected ArrayList<Double> _Path;
    protected int _indexPath;

    protected LinkedAnalysisCollection _AnalysisLinkedToSweep;

    public LoggerFrameSweep(String name, String xUnit, String yUnit) {
        super(name);
        _name = name;
        _xUnit = xUnit;
        _yUnit = yUnit;

        _AnalysisLinkedToSweep = new LinkedAnalysisCollection();

        _logger = new LoggerFrameMinimal(false, true, false, true);
        _logger.setTitle(name);
        _logger.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _logger.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _logger.setVisible(false);
                if (getisRunning()) {
                    kill();
                }
            }
        });

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

    protected boolean useLogPath = false;

    public void InitSweep(double Start, double Finish, double StepOrFactor, int times, boolean UpDown,
            int millisDelay) {
        if (useLogPath)
            calculate_LogPATH(Start, Finish, StepOrFactor, UpDown);
        else
            calculate_PATH(Start, Finish, StepOrFactor, UpDown);
        _Ncycle = times;
        _ExecutionDelay = millisDelay;

        if (_Ncycle < 0)
            _Ncycle = Integer.MAX_VALUE;

        _cycleCounter = 0;
        _indexPath = 0;

        _logger.setVisible(true);
    }

    public abstract void sweepAction(double value);

    @Override
    public void StartLoop() {
        if (_indexPath == 0 || !_logger.getLoadedDataset().getSeries().contains(_points)) {
            String newKey = _name + Math.round((System.currentTimeMillis() - GlobalVar.start) * 1e-3);
            _points = new XYSeries(newKey, false);
            _logger.addXYSeries(getSeries(), _xUnit, _yUnit);
            _logger.DisplayXYSeries(getSeries().getKey());
        }

        sweepAction(_Path.get(_indexPath++));
    }

    @Override
    public void EndLoop() {
        if (_indexPath == _Path.size()) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        _AnalysisLinkedToSweep.fireAnalyzesExecution(_points);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    public LoggerFrame get_logger() {
        return _logger;
    }

    public LinkedAnalysisCollection get_AnalysisLinkedToSweep() {
        return _AnalysisLinkedToSweep;
    }

    public String get_name() {
        return _name;
    }
}
