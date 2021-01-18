package LoggerCore.Zefiro.ITCharacteristic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.event.*;

import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;

import LoggerCore.PointsStream;
import LoggerCore.GlobalVar;
import LoggerCore.LoggerAppMinimal;
import LoggerCore.Zefiro.Zefiro;
import LoggerCore.themal.IFeedbackController;

public class ITCharacteristic extends PointsStream {

    private Zefiro _zef;
    private IFeedbackController _feedbackController;
    private LoggerAppMinimal _logger;

    protected int _Ncycle;
    protected int _cycleCounter;
    protected ArrayList<Double> _Path;
    protected int _indexPath;

    public ITCharacteristic(Zefiro zef, IFeedbackController feedbackController) {
        super("ITCharacteristic");
        _zef = zef;
        _feedbackController = feedbackController;

        _logger = new LoggerAppMinimal(false, true);
        _logger.setTitle("ITCharacteristic");
        _logger.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _logger.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _logger.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _logger.setVisible(false);
                if (getisRunning())
                    kill();
            }
        });

        _logger.addToAutosave("ITCharacteristic");

        _firstDelay = true;
        _secondDelay = false;
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

        _logger.setVisible(true);
        return super.setup(arg);
    }

    @Override
    public void StartLoop() {
        if (_indexPath == 0) {
            String newKey = "ITCharacteristic" + Math.round((System.currentTimeMillis() - GlobalVar.start) * 1e-3);
            _points = new XYSeries(newKey, false);
            _logger.addXYSeries(getSeries(), "Temperature[°C]", "Current[mA]");
            _logger.DisplayXYSeries(getSeries().getKey());
        }
        _feedbackController.set_target_value(_Path.get(_indexPath++));
    }

    @Override
    public Point2D acquirePoint() {
        double temperature = _zef.getTemperatureHeater();
        double current = _zef.readCurrentSensor();
        return new Point2D.Double(temperature, current);
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
    public synchronized void kill() {
        super.kill();
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
}
