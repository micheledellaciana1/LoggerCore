package LoggerCore.Zefiro.IVCharacteristic;

import LoggerCore.Sweep;
import LoggerCore.Zefiro.Zefiro;

public class VoltageSweep extends Sweep {

    protected Zefiro _zefiro;

    public VoltageSweep(Zefiro zefiro) {
        super();
        _zefiro = zefiro;
    }

    @Override
    public void sweepAction(double value) {
        _zefiro.setVoltageFallSensor(value);
    }
}
