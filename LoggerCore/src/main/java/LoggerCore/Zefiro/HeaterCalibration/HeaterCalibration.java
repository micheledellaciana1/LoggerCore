package LoggerCore.Zefiro.HeaterCalibration;

import java.awt.geom.Point2D;
import java.awt.event.*;
import java.awt.*;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.PointsStream;
import LoggerCore.Zefiro.Zefiro;
import LoggerCore.themal.IFeedbackController;
import LoggerCore.themal.LookUpTable;

public class HeaterCalibration extends PointsStream {

    protected double _alpha;
    protected double _beta;
    protected Zefiro _zef;
    protected LoggerFrameMinimal _logger;
    protected double _exitationCurrent;
    protected IFeedbackController _feedbackHeater;

    protected int _NAvernage;

    public HeaterCalibration(Zefiro zef, IFeedbackController feedbackHeater) {
        super("Heater resistance");
        _zef = zef;
        _feedbackHeater = feedbackHeater;

        _logger = new LoggerFrameMinimal(false, true, false);
        _logger.setTitle("IVCharacteristic");
        _logger.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _logger.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _logger.setVisible(false);
                if (getisRunning())
                    kill();
            }
        });

        _logger.addToAutosave("Heater_Calibration");
    }

    private boolean _stateFeedbackAtSetup;

    @Override
    public boolean setup(Object arg) {
        try {
            String args[] = String.class.cast(arg).split(" ");

            _exitationCurrent = Double.valueOf(args[0]);
            _NAvernage = Integer.valueOf(args[1]);
            setExecutionDelay(Integer.valueOf(args[2]));
            _alpha = Double.valueOf(args[3]);
            _beta = Double.valueOf(args[4]);

            int ActualSecond = (int) (Math.round(System.currentTimeMillis() - GlobalVar.start) * 1e-3);
            _points = new XYSeries("ScatterChart" + ActualSecond, false);

            _logger.addXYSeries(_points, "Chamber Temperature [°C]", "Heater Resistance [Ohm]");
            _logger.DisplayXYSeries(_points.getKey());

            _logger.setVisible(true);
            _stateFeedbackAtSetup = _feedbackHeater.getFeedbackON();
            _feedbackHeater.setFeedbackON(false);

            return true;
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();

            return false;
        }
    }

    public XYSeries calculateZefiroLUT(double R0, double T0, double Tmin, double Tmax) {
        XYSeries _calibration = new XYSeries(
                "Calibration" + (int) (Math.round(System.currentTimeMillis() - GlobalVar.start) * 1e-3), false);
        LookUpTable LUT = new LookUpTable(new ArrayList<Double>(), new ArrayList<Double>());

        for (double Temp = Tmin; Temp < Tmax; Temp += 0.5) {
            LUT.get_X().add(Temp);
            double correspondingResistance = R0 * (1 + _alpha * Temp + _beta * Temp * Temp)
                    / (1 + _alpha * T0 + _beta * T0 * T0);
            LUT.get_Y().add(correspondingResistance);
            _calibration.add(Temp, correspondingResistance, false);
        }

        _zef.set_LUTHeater(LUT);

        return _calibration;
    }

    @Override
    public Point2D acquirePoint() {
        double ResistanceHeater = Double.class
                .cast(_zef.executeCommand("ReadHeaterResistance_withExitation", _exitationCurrent));
        double ChamberTemperature = Double.class.cast(_zef.executeCommand("ReadTempSensor", null));

        return new Point2D.Double(ChamberTemperature, ResistanceHeater);
    }

    @Override
    public void EndLoop() {
        if (_points.getItemCount() >= _NAvernage) {
            calibrate();
            kill();
        }
        super.EndLoop();
    }

    @Override
    public synchronized void kill() {
        _feedbackHeater.setFeedbackON(_stateFeedbackAtSetup);
        super.kill();
    }

    public void calibrate() {
        double RHeaterAveg = 0;
        double CTempAveg = 0;

        for (int i = 0; i < _points.getItemCount(); i++) {
            RHeaterAveg += _points.getDataItem(i).getYValue();
            CTempAveg += _points.getDataItem(i).getXValue();
        }

        RHeaterAveg /= _points.getItemCount();
        CTempAveg /= _points.getItemCount();

        final XYSeries calibrationSeries = calculateZefiroLUT(RHeaterAveg, CTempAveg, -273.15, 2000);
        _logger.addXYSeries(calibrationSeries, "Temperature [°C]", "Resistance [Ohm]");

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                _logger.DisplayXYSeries(calibrationSeries.getKey());
            }
        });
    }
}
