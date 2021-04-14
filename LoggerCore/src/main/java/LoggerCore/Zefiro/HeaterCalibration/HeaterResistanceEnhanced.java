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

public class HeaterResistanceEnhanced extends PointsStream {

    protected Zefiro _zef;
    protected LoggerFrameMinimal _logger;
    protected double _exitationCurrent;

    public HeaterResistanceEnhanced(Zefiro zef) {
        super("Heater resistance");
        _zef = zef;

        _ExecutionDelay = 5;
        _logger = new LoggerFrameMinimal(true, true, true, false);
        _logger.setTitle("Heater resistance enhanced");
        _logger.addXYSeries(_points, "Time [sec]", "Heater resistance [Ohm]");
        _logger.DisplayXYSeries(_points.getKey());

        _logger.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _logger.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _logger.setVisible(false);
                if (getisRunning())
                    kill();
            }
        });
    }

    private boolean _stateFeedbackAtSetup;

    @Override
    public boolean setup(Object arg) {
        try {
            String args[] = String.class.cast(arg).split(" ");

            _exitationCurrent = Double.valueOf(args[0]);
            _logger.EraseDataSeries();
            _logger.setVisible(true);

            return true;
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();

            return false;
        }
    }

    @Override
    public Point2D acquirePoint() {
        double ResistanceHeater = Double.class
                .cast(_zef.executeCommand("ReadHeaterResistance_withExitation_ENHANCED", _exitationCurrent));
        double time = Double.class.cast((System.currentTimeMillis() - GlobalVar.start) * 1e-3);

        return new Point2D.Double(time, ResistanceHeater);
    }
}
