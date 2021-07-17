package LoggerCore.Zefiro;

import LoggerCore.LoggerFrameSweep;
import java.awt.geom.*;

public class ILEDICharacteristic extends LoggerFrameSweep {

    private Zefiro _zef;

    public ILEDICharacteristic(Zefiro zef) {
        super("ILEDICharacteristic", "ILED[V]", "Current[mA]");
        _zef = zef;

        _logger.addToAutosave("IVCharacteristic");

        _firstDelay = true;
        _secondDelay = false;
    }

    @Override
    public Point2D acquirePoint() {
        double currentLED = _zef.get_CurrentLED();
        double current = _zef.readCurrentSensor();
        return new Point2D.Double(currentLED, current);
    }

    @Override
    public void sweepAction(double value) {
        _zef.setCurrentLED(value);
    }
}
