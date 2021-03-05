package LoggerCore.Zefiro.IVCharacteristic;

import java.awt.geom.Point2D;

import javax.swing.JMenu;

import LoggerCore.LoggerFrameSweep;
import LoggerCore.Zefiro.Zefiro;

public class IVCharacteristic extends LoggerFrameSweep {

    private Zefiro _zef;
    JMenu analysisMenu;

    public IVCharacteristic(Zefiro zef) {
        super("IVCharacteristic", "Voltage[V]", "Current[mA]");
        _zef = zef;

        LinearFitFrame LFF = new LinearFitFrame(_AnalysisLinkedToSweep);
        PowerLawFitFrame PLFF = new PowerLawFitFrame(_AnalysisLinkedToSweep);
        JMenu analysisMenu = new JMenu("Analysis");
        analysisMenu.add(LFF.BuildShowFrameMenuItem());
        analysisMenu.add(PLFF.BuildShowFrameMenuItem());
        _logger.getJMenuBar().add(analysisMenu);

        _logger.addToAutosave("IVCharacteristic");

        _firstDelay = true;
        _secondDelay = false;
    }

    @Override
    public Point2D acquirePoint() {
        double voltage = _zef.readVoltageFallSensor();
        double current = _zef.readCurrentSensor();
        return new Point2D.Double(voltage, current);
    }

    @Override
    public void sweepAction(double value) {
        _zef.setVoltageFallSensor(value);
    }
}
