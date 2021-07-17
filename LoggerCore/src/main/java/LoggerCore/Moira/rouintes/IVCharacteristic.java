package LoggerCore.Moira.rouintes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JMenu;

import LoggerCore.LoggerFrameSweep;
import LoggerCore.Moira.Moira;

import LoggerCore.Zefiro.IVCharacteristic.LinearFitFrame;
import LoggerCore.Zefiro.IVCharacteristic.PowerLawFitFrame;

public class IVCharacteristic extends LoggerFrameSweep {

    private Moira _moira;
    JMenu analysisMenu;

    public IVCharacteristic(Moira moira) {
        super("IVCharacteristic", "Voltage[V]", "Current[A]");
        _moira = moira;

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
        double voltage = 0;
        double current = 0;

        ArrayList<double[]> voltages = null;

        do {
            voltages = _moira.ReadOscilloscopeBothChannel();
        } while (voltages == null);

        voltage = Arrays.stream(voltages.get(0)).average().getAsDouble();
        current = _moira.convertToCurrent(Arrays.stream(voltages.get(1)).average().getAsDouble());

        return new Point2D.Double(voltage, current);
    }

    @Override
    public void sweepAction(double value) {
        switch (_moira.getMoiraState()) {
            case DirectCoupled:
                if (_indexPath + 1 < _Path.size())
                    _moira.setDCVoltage(0, _Path.get(_indexPath++));
                break;
            case LEDCoupled:
                if (_indexPath + 1 < _Path.size())
                    _moira.setDCVoltage(1, _Path.get(_indexPath++));
                break;
            default:
                break;
        }
    }
}